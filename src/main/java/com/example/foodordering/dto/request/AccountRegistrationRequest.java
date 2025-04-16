package com.example.foodordering.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRegistrationRequest {

    String firstname;
    String lastname;
    String email;
    String phoneNumber;
    String role;

    @Size(min = 3, max = 50, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
}
