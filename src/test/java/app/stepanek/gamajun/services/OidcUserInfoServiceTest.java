package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OidcUserInfoServiceTest {
    @Mock
    UserDao userDao;

    @InjectMocks
    OidcUserInfoService oidcUserInfoService;

    @Test
    void loadUser() {
        when(userDao.findByUsername("username"))
                .thenReturn(
                        Optional.ofNullable(User
                                .builder()
                                .username("username")
                                .name("name")
                                .surname("surname")
                                .email("email")
                                .roles(new HashSet<>())
                                .build())
                );

        var oidcUserInfo = oidcUserInfoService.loadUser("username");

        assertEquals("name", oidcUserInfo.getFullName());
        assertEquals("email", oidcUserInfo.getEmail());
        assertEquals("username", oidcUserInfo.getNickName());
    }
}