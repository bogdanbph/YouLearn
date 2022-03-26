package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.UserRole;
import com.youlearn.youlearn.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.youlearn.youlearn.utils.Constants.USER_NOT_FOUND_MSG;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private final Logger logger = LogManager.getLogger(UserService.class);

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.info("Loading user by email from database...");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }

        return optionalUser.get();
    }

    public User getUserByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }

        return byEmail.get();
    }

    public String signUp(User user) {
        boolean present = userRepository.findByEmail(user.getEmail()).isPresent();

        // TODO: check if user is in database, but he didnt confirmed yet.
        if (present) {
            throw new BadRequestException("Email is already present in database.");
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

    public void deleteUser(User user) {
        userRepository.delete(user);
        tokenService.deleteTokenByUserId(user.getId());
    }

    public void enableUser(String email) {
        userRepository.enableAppUser(email);
    }

    public UserRole getUserRoleForUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get().getRole();
        }
        else {
            throw new BadRequestException("User not present in database.");
        }
    }
}
