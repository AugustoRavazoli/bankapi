package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

record CustomerRequest (

  @Pattern(
    regexp = "[A-Za-z\\s]*", 
    message = "must contains only alphabetic characters and whitespaces"
  )
  @NotBlank
  String name,

  @Email
  @NotBlank
  String email,

  @CPF
  @NotBlank
  String cpf,

  @Past
  @NotNull
  LocalDate birthDate

) {}
