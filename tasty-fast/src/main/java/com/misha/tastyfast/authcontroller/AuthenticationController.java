package com.misha.tastyfast.authcontroller;

import com.misha.tastyfast.requests.registrationRequests.RegistrationRequest;
import com.misha.tastyfast.security.AuthenticationRequest;
import com.misha.tastyfast.security.AuthenticationResponse;
import com.misha.tastyfast.security.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")

    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
            authenticationService.registerUser(request);
            return ResponseEntity.ok("User register successfully");
    }

    @PostMapping("/register/business")
    public ResponseEntity<?> registerBusinessAccount(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        authenticationService.registerBusinessAccount(request);
        return ResponseEntity.ok("User register successfully");
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        authenticationService.registerAdmin(request);
        return ResponseEntity.ok("User register successfully");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest
    ){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }




}
