package htw.berlin.microservices.composite.chart;

import static htw.berlin.api.event.Event.Type.CREATE;
import static htw.berlin.api.event.Event.Type.DELETE;
import static htw.berlin.microservices.composite.chart.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import htw.berlin.api.core.chart.Chart;
import htw.berlin.api.core.chart.ChartLabel;
import htw.berlin.api.core.chart.ChartType;
import htw.berlin.api.event.Event;
import org.junit.jupiter.api.Test;

public class IsSameEventTests{

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventObjectCompare() throws JsonProcessingException {


        Event<Integer, Chart> event1 = new Event<>(CREATE, 1, new Chart(1, "se01",ChartType.LINE, ChartLabel.GRADES, null, null, null));
        Event<Integer, Chart> event2 = new Event<>(CREATE, 1, new Chart(1, "se01",ChartType.LINE, ChartLabel.GRADES, null, null, null));
        Event<Integer, Chart> event3 = new Event<>(DELETE, 1, null);
        Event<Integer, Chart> event4 = new Event<>(CREATE, 1, new Chart(2, "se01",ChartType.LINE, ChartLabel.GRADES, null, null, null));

        String event1Json = mapper.writeValueAsString(event1);

        assertThat(event1Json, is(sameEventExceptCreatedAt(event2)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event3)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event4)));
    }

}
