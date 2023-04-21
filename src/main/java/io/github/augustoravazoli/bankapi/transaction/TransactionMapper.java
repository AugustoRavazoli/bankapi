package io.github.augustoravazoli.bankapi.transaction;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface TransactionMapper {

  Transaction toEntity(TransactionRequest transaction);

  TransactionResponse toResponse(Transaction transaction);

}
