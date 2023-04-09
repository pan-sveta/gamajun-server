package app.stepanek.gamajun.security;

import app.stepanek.gamajun.services.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public class GamajunAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    public GamajunAuthenticationConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString("sub");

        var user = userService.findByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                new GamajunUserPrincipal(user), user.getPassword(), user.getRoles());

    }
}
