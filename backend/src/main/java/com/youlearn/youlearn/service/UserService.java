package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.youlearn.youlearn.utils.Constants.USER_NOT_FOUND_MSG;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUp(User user) {
        boolean present = userRepository.findByEmail(user.getEmail()).isPresent();

        // TODO: check if user is in database, but he didnt confirmed yet.
        if (present) {
            throw new BadRequestException("400", "Email is already present in database.");
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        userRepository.save(user);

        String tokenString = UUID.randomUUID().toString();
        Token token = new Token(
                tokenString,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                user
        );
        tokenService.saveToken(token);

        return tokenString;
    }

    public int enableUser(String email) {
        return userRepository.enableAppUser(email);
    }
}
