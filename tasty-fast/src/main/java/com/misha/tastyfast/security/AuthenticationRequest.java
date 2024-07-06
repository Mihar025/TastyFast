package com.misha.tastyfast.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringBootVersion;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @Email(message = "Email is not well formatted! \n Example user123@mail.com")
    @NotEmpty(message = "Email field cannot be empty")
    @NotNull(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password field cannot be empty")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password should be minimum 8 long characters")
    private String password;


}
