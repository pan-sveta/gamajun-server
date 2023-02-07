package app.stepanek.gamajun.services;

import app.stepanek.gamajun.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GamajunUserService implements UserDetailsService {

    UserRepository userRepository;

    public GamajunUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GamajunUserService() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).get();
    }
}
