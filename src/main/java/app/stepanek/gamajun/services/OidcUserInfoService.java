package app.stepanek.gamajun.services;

import app.stepanek.gamajun.repository.UserRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Example service to perform lookup of user info for customizing an {@code id_token}.
 */
@Service
public class OidcUserInfoService {

    private final UserRepository userRepository;

    public OidcUserInfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public OidcUserInfo loadUser(String username) {
        var user = userRepository.findByUsername(username).get();

        return OidcUserInfo
                .builder()
                .name(user.getName())
                .familyName(user.getSurname())
                .nickname(user.getUsername())
                .build();
    }


}
