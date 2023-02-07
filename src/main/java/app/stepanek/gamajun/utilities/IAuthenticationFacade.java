package app.stepanek.gamajun.utilities;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    String getPrincipal();
    String getUsername();
}
