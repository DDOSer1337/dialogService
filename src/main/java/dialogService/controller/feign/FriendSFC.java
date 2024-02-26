package dialogService.controller.feign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Headers("Content-Type: application/json")
@FeignClient("${application.feign-client.friend}")
public interface FriendSFC {
    @GetMapping(value = "api/v1/friends/blockFriendId", consumes = "application/json")
    List<UUID> isBlocked();
}
