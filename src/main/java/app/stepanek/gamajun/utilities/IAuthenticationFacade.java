package app.stepanek.gamajun.utilities;

import app.stepanek.gamajun.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    Jwt getPrincipal();
    String getUsername();
    User getUser();
}
