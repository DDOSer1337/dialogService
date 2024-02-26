package dialogService.controller.feign;

import feign.Headers;
import org.javapro_team44.dto.authorization.AuthenticateResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Headers("Content-Type: application/json")
@FeignClient("${application.feign-client.auth}")
public interface AuthorizationSFC {
    @PostMapping(value = "/api/v1/auth/checkAccessToken", consumes = "application/json")
    Boolean tokenVerify(@RequestBody AuthenticateResponseDto tokenDTO);
}

