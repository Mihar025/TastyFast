package com.misha.tastyfast.security;

import com.misha.tastyfast.exception.AccountDissabledException;
import com.misha.tastyfast.exception.EmailorPasswordAlreadyExistException;
import com.misha.tastyfast.exception.UserAlreadyExistException;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.model.UserRoles;
import com.misha.tastyfast.repositories.RoleRepository;
import com.misha.tastyfast.repositories.TokenRepository;
import com.misha.tastyfast.repositories.UserRepository;
import com.misha.tastyfast.requests.registrationRequests.RegistrationBusinessAccountRequest;
import com.misha.tastyfast.requests.registrationRequests.RegistrationRequest;
import com.misha.tastyfast.role.Role;
import com.misha.tastyfast.services.EmailService;
import com.misha.tastyfast.services.EmailTemplateName;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;


    public void register(RegistrationRequest request, UserRoles userRoles ) throws MessagingException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailorPasswordAlreadyExistException("User with email: " +  request.getEmail() + " already exist");
        }
        Role userRole = roleRepository.findByName(userRoles.name())
                .orElseThrow(() -> new IllegalStateException("ROLE"+ userRoles.name()+  " was not initiated"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(Collections.singletonList(userRole))
                .build();

        try {
            userRepository.save(user);
            sendValidationEmail(user);
        } catch (DataIntegrityViolationException e){
            throw new EmailorPasswordAlreadyExistException("User with email: " + request.getEmail() + " already exist");
        }
    }
    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )

            );
            var claims = new HashMap<String, Object>();
            var user = ((User) auth.getPrincipal());
            claims.put("fullName", user.getFullName());

            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder()
                    .token(jwtToken).build();
        } catch (BadCredentialsException e){
            throw new EmailorPasswordAlreadyExistException("Invalid email or password");
        } catch (DisabledException e){
            throw new AccountDissabledException("Account is disabled");
        }



    }

    public AuthenticationResponse authenticateBusinessAccount(AuthenticationRequest authenticationRequest){
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )

            );
            var claims = new HashMap<String, Object>();
            var user = ((User) auth.getPrincipal());
            claims.put("fullName", user.getFullName());

            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder()
                    .token(jwtToken).build();
        } catch (BadCredentialsException e){
            throw new EmailorPasswordAlreadyExistException("Invalid email or password");
        } catch (DisabledException e){
            throw new AccountDissabledException("Account is disabled");
        }

    }

    // @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }
        var user = userRepository.findById(savedToken.getUser().getId())

                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    public void registerUser(RegistrationRequest request) throws MessagingException{
        register(request, UserRoles.USER);
    }

    public void registerBusinessAccount(RegistrationRequest request) throws MessagingException{
        register(request, UserRoles.BUSINESS_OWNER);
    }


    public void registerAdmin(RegistrationRequest request) throws MessagingException{
        register(request, UserRoles.ADMIN);
    }

}