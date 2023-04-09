package io.github.augustoravazoli.bankapi.customer;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface CustomerMapper {

  Customer toEntity(CustomerRequest customer);

  CustomerResponse toResponse(Customer customer);

}
