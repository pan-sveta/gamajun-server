package app.stepanek.gamajun.utilities;

import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    private final UserService userService;

    public AuthenticationFacade(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Jwt getPrincipal() {
        if (getAuthentication().getPrincipal() instanceof Jwt)
            return (Jwt) getAuthentication().getPrincipal();
        else
            throw new RuntimeException("Authenticaiton principal is not type of 'Jwt'");
    }

    @Override
    public String getUsername() {
        return getPrincipal().getSubject();
    }

    @Override
    public User getUser() {
        return userService.findByUsername(getUsername());
    }


}
