package org.cyberrealm.tech.bazario.backend.security;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmailOrPhoneNumber(username, username).orElseThrow(() ->
                new UsernameNotFoundException("Can't find user by email or phone: "
                        + username));
    }
}
