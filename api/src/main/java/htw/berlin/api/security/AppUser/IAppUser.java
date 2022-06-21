package htw.berlin.api.security.AppUser;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface IAppUser {
    @PostMapping(
            value = "/user",
            consumes = "application/json"
    )
    Mono<Void> creatUser(@RequestBody AppUser body);
}
