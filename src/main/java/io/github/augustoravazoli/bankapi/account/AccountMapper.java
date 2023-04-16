package io.github.augustoravazoli.bankapi.account;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface AccountMapper {

  AccountResponse toResponse(Account account);

}
