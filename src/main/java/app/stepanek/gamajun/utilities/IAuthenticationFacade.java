package app.stepanek.gamajun.utilities;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    OAuth2AuthenticatedPrincipal getPrincipal();
    String getUsername();
}
