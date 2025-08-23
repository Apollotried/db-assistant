package com.marouane.db_assistant.auth;


import com.marouane.db_assistant.role.RoleRepository;
import com.marouane.db_assistant.security.JwtService;
import com.marouane.db_assistant.user.User;
import com.marouane.db_assistant.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
                //todo - better exceptions
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.fullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}
