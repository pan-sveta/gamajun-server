package app.stepanek.gamajun.services;

import app.stepanek.gamajun.repository.UserDao;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GamajunUserDetailService implements UserDetailsService {

    private final UserDao userDao;

    public GamajunUserDetailService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        List<GrantedAuthority> authorityList = new ArrayList<>();

        for (var role : user.getRoles())
            authorityList.add(new SimpleGrantedAuthority(role.getName()));


        return User
                .builder()
                .authorities(authorityList)
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
