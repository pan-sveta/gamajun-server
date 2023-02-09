package app.stepanek.gamajun.utilities;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    Jwt getPrincipal();
    String getUsername();
}
