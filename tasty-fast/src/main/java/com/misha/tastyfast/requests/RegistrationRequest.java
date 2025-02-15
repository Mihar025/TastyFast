package com.misha.tastyfast.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    private Integer id;

    @NotEmpty(message = "Firstname cannot be empty")
    @NotNull( message = "Firstname is mandatory ")
    private String firstname;

    @NotEmpty(message = "Lastname cannot be empty")
    @NotNull(message = "Lastname is mandatory")
    private String lastname;
    @Email(message = "Email is not well formatted! \n Example user123@mail.com")
    @NotEmpty(message = "Email field cannot be empty")
    @NotNull(message = "Email is mandatory")
    private String email;

    @NotEmpty(message = "Password field cannot be empty")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password should be minimum 8 long characters")
    private String password;


}
