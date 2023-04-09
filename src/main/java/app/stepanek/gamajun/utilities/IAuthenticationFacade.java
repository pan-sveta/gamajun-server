package app.stepanek.gamajun.utilities;

import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.security.GamajunUserPrincipal;
import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    GamajunUserPrincipal getPrincipal();
    String getUsername();
    User getUser();
    boolean isResourceOwner(User entityOwner);
}
