package app.stepanek.gamajun.utilities;

import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.security.GamajunUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            throw new RuntimeException("Authenticaiton principal is not type of 'GamajunUserPrincipal'");
    }

    @Override
    public String getUsername() {
        return getPrincipal().getUsername();
    }

    @Override
    public User getUser() {
        return getPrincipal().user();
    }

    @Override
    public boolean isResourceOwner(User entityOwner) {
        if (getUser().getRoles().stream().anyMatch(role -> role.getName().equals("GAMAJUN_TEACHER")))
            return true;
        else
            return getUser().equals(entityOwner);
    }
}
