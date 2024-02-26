package dialogService.JWUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    public String getId(String token) {
        return getAllClaimsFromToken(token).get("id", String.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        int i = token.lastIndexOf('.');
        String withoutSignature = token.substring(0, i+1);
        return Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
    }

}