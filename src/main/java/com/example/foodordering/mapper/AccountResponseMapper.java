package com.example.foodordering.mapper;

import com.example.foodordering.dto.request.AccountRegistrationRequest;
import com.example.foodordering.dto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountResponseMapper {

    AccountResponse toAccountResponse(AccountRegistrationRequest accountRegistrationRequest);
}
