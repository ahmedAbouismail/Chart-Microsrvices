: ${HOST=localhost}
: ${PORT=8443}
: ${CHA_ID_DAT=1}
: ${CHA_ID_NOT_FOUND=13}
: ${CHA_ID_NO_DAT=113}
: ${SKIP_CB_TESTS=false}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo  "- Failing command: $curlCmd"
    echo  "- Response Body: $RESPONSE"
    exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
  url=$@
  if $url -ks -f -o /dev/null
  then
    return 0
  else
    return 1
  fi;
}

function waitForService() {
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url
  do
    n=$((n + 1))
    if [[ $n == 100 ]]
    then
      echo " Give up"
      exit 1
    else
      sleep 3
      echo -n ", retry #$n "
    fi
  done
  echo "DONE, continues..."
}

function testCompositeCreated() {

    # Expect that the Product Composite for productId $PROD_ID_REVS_RECS has been created with three recommendations and three reviews
    if ! assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_DAT -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$CHA_ID_DAT" $(echo $RESPONSE | jq .chartId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
  local chartId=$1
  local composite=$2

  assertCurl 202 "curl -X DELETE $AUTH -k https://$HOST:$PORT/chart-composite/${chartId} -s"
  assertEqual 202 $(curl -X POST -s -k https://$HOST:$PORT/chart-composite -H "Content-Type: application/json" -H "Authorization: Bearer $ACCESS_TOKEN" --data "$composite" -w "%{http_code}")
}

function setupTestdata() {

  body="{\"chartId\":$CHA_ID_NO_DAT"
    body+=\
',"studentId":"string","chartLabel":"GRADES","chartType":"BAR","createdAt":"","lastUpdate":"","serviceAddresses":{"cmc":"string","cha":"string","dat":"string"}}'
  recreateComposite "$CHA_ID_NO_DAT" "$body"

    body="{\"chartId\":$CHA_ID_DAT"
    body+=\
',"chartLabel":"GRADES","chartType":"BAR","createdAt":"","lastUpdate":"","dataSummery":{"dataId":0,"studentId":"string","transcripts":[{"semester":"string","grades":[{"grade":10,"module":"mathe","label":"string","credits":5},{"grade":10,"module":"mecha","label":"string","credits":5}],"creditsTotal":0,"average":0}],"createdAt":"","lastUpdate":""},"serviceAddresses":{"cmc":"string","cha":"string","dat":"string"}}'
  recreateComposite "$CHA_ID_DAT" "$body"

}
set -e

echo "Start Tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"
echo "SKIP_CB_TESTS=${SKIP_CB_TESTS}"

if [[ $@ == *"start"* ]]
then
  echo "Restarting the test environment..."
  echo "$ docker-compose down --remove-orphans"
  docker-compose down --remove-orphans
  echo "$ docker-compose up -d"
  docker-compose up -d
fi

waitForService curl -k https://$HOST:$PORT/actuator/health

ACCESS_TOKEN=$(curl -k https://writer:secret@$HOST:$PORT/oauth2/token -d grant_type=client_credentials -s | jq .access_token -r)
echo ACCESS_TOKEN=$ACCESS_TOKEN
AUTH="-H \"Authorization: Bearer $ACCESS_TOKEN\""

# Verify access to Eureka
assertCurl 200 "curl -H "accept:application/json" -k https://u:p@$HOST:$PORT/eureka/api/apps -s"
assertEqual 5 $(echo $RESPONSE | jq ".applications.application | length")

# Verify access to the Config server
assertCurl 200 "curl -H "accept:application/json" -k https://dev-usr:dev-pwd@$HOST:$PORT/config/product/docker -s"
TEST_VALUE="hello world"
ENCRYPTED_VALUE=$(curl -k https://dev-usr:dev-pwd@$HOST:$PORT/config/encrypt --data-urlencode "$TEST_VALUE" -s)
DECRYPTED_VALUE=$(curl -k https://dev-usr:dev-pwd@$HOST:$PORT/config/decrypt -d $ENCRYPTED_VALUE -s)
assertEqual "$TEST_VALUE" "$DECRYPTED_VALUE"

setupTestdata

waitForMessageProcessing

# Verify that a normal request works
assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_DAT -s"
assertEqual $CHA_ID_DAT $(echo $RESPONSE | jq .chartId)

# Verify that a 404 (Not Found) error is returned for a non-existing chartId
assertCurl 404 "curl $AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_NOT_FOUND -s"
assertEqual "No chart found for chartId: $CHA_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

# Verify that no data are returned for chartId $CHA_ID_NO_DAT
assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_NO_DAT -s"
assertEqual $PROD_ID_NO_RECS $(echo $RESPONSE | jq .chartId)

# Verify that a 422 (Unprocessable Entity) error is returned for (-1)
assertCurl 422 "curl $AUTH -k https://$HOST:$PORT/chart-composite/-1 -s"
assertEqual "\"Invalid chartId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error is returned for invalid format
assertCurl 400 "curl $AUTH -k https://$HOST:$PORT/chart-composite/invalidChartId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

# Verify that a request without access token 401, Unauthorized
assertCurl 401 "curl -k https://$HOST:$PORT/chart-composite/$CHA_ID_DAT -s"

# Verify that the reader can call the read API but not delete API.
READER_ACCESS_TOKEN=$(curl -k https://reader:secret@$HOST:$PORT/oauth2/token -d grant_type=client_credentials -s | jq .access_token -r)
echo READER_ACCESS_TOKEN=$READER_ACCESS_TOKEN
READER_AUTH="-H \"Authorization: Bearer $READER_ACCESS_TOKEN\""

assertCurl 200 "curl $READER_AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_DAT -s"
assertCurl 403 "curl -X DELETE $READER_AUTH -k https://$HOST:$PORT/chart-composite/$CHA_ID_DAT -s"

# Verify access to Swagger and OpenAPI URLs
echo "Swagger/OpenAPI tests"
assertCurl 307 "curl -ks  https://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -ksL https://$HOST:$PORT/openapi/swagger-ui.html"
assertCurl 200 "curl -ks  https://$HOST:$PORT/openapi/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config"
assertCurl 200 "curl -ks  https://$HOST:$PORT/openapi/v3/api-docs"
assertEqual "3.0.1" "$(echo $RESPONSE | jq -r .openapi)"
assertEqual "https://$HOST:$PORT" "$(echo $RESPONSE | jq -r .servers[].url)"
assertCurl 200 "curl -ks  https://$HOST:$PORT/openapi/v3/api-docs.yaml"


if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End, all tests OK:" `date`