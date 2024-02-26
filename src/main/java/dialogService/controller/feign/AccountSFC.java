package dialogService.controller.feign;

import feign.Headers;
import org.javapro_team44.dto.account.AccountDTO;
import org.javapro_team44.dto.account.AccountSecureDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Headers("Content-Type: application/json")
@FeignClient("${application.feign-client.account}")
public interface AccountSFC {
    @GetMapping(value = "/api/v1/account", consumes = "application/json")
    AccountSecureDTO getAccount(@RequestHeader("Authorization") String token, @RequestParam String email);

    @GetMapping(value = "/api/v1/account/{id}", consumes = "application/json")
    AccountDTO getAccountById(@RequestHeader("Authorization") String token, @PathVariable UUID id);
}