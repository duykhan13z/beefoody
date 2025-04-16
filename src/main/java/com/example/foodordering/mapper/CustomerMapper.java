package com.example.foodordering.mapper;
import com.example.foodordering.dto.request.AccountRegistrationRequest;
import com.example.foodordering.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toCustomer(AccountRegistrationRequest accountRegistrationRequest);
}
