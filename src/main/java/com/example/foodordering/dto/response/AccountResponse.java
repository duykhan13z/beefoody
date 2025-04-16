package com.example.foodordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AccountResponse {

    Integer accountId;

    String firstname;
    String lastname;
    String email;
    String phoneNumber;

    String role;
    String username;
    String password;

}
