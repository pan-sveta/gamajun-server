package app.stepanek.gamajun.utilities;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Jwt getPrincipal() {
        if (getAuthentication().getPrincipal() instanceof Jwt)
            return (Jwt) getAuthentication().getPrincipal();
        else
            throw new RuntimeException("Authenticaiton principal is not type of 'OAuth2AuthenticatedPrincipal'");
    }

    @Override
    public String getUsername() {
        return getPrincipal().getSubject();
    }
}
