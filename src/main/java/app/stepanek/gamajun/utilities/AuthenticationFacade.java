package app.stepanek.gamajun.utilities;

import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.security.GamajunUserPrincipal;
import app.stepanek.gamajun.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    public AuthenticationFacade() {
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public GamajunUserPrincipal getPrincipal() {
        if (getAuthentication().getPrincipal() instanceof GamajunUserPrincipal)
            return (GamajunUserPrincipal) getAuthentication().getPrincipal();
        else
            throw new RuntimeException("Authenticaiton principal i" +
                    "s not type of 'Jwt'");
    }

    @Override
    public String getUsername() {
        return getPrincipal().getUsername();
    }

    @Override
    public User getUser() {
        return getPrincipal().getUser();
    }


}
