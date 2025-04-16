package com.example.foodordering.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AccountUpdateRequest {

    String firstname;
    String lastname;
    String password;
    String email;
    String phoneNumber;


}
