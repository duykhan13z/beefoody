package com.example.foodordering.mapper;
import com.example.foodordering.dto.request.AccountRegistrationRequest;
import com.example.foodordering.dto.request.AccountUpdateRequest;
import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toAccountResponse(Account account);
    void updateAccount(@MappingTarget Account account, AccountUpdateRequest request);

    @Mapping(target = "password", ignore = true)
    Account toAccount(AccountRegistrationRequest accountRegistrationRequest);

}
