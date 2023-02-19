package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Role;
import app.stepanek.gamajun.repository.UserDao;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Example service to perform lookup of user info for customizing an {@code id_token}.
 */
@Service
public class OidcUserInfoService {

    private final UserDao userDao;

    public OidcUserInfoService(UserDao userDao) {
        this.userDao = userDao;
    }

    public OidcUserInfo loadUser(String username) {
        var user = userDao.findByUsername(username).get();


        return OidcUserInfo
                .builder()
                .name(user.getName())
                .familyName(user.getSurname())
                .nickname(user.getUsername())
                .email(user.getEmail())
                .picture("https://ui-avatars.com/api/?background=random&name=%s".formatted(user.getFullName()))
                .claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }


}
