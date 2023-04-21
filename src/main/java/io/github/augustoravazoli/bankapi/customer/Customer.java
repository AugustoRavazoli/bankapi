package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.br.CPF;
import static jakarta.persistence.CascadeType.ALL;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import io.github.augustoravazoli.bankapi.Application.Default;
import io.github.augustoravazoli.bankapi.account.Account;

@Entity
public class Customer {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Pattern(
    regexp = "[A-Za-z\\s]*", 
    message = "must contains only alphabetic characters and whitespaces"
  )
  @Column(nullable = false)
  private String name;

  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @CPF
  @NaturalId
  @Column(nullable = false, unique = true)
  private String cpf;

  @Past
  @Column(nullable = false)
  private LocalDate birthDate;

  @OneToMany(mappedBy = "owner", cascade = ALL, fetch = LAZY, orphanRemoval = true)
  private List<Account> accounts = new ArrayList<>();

  public Customer() {}

  @Default
  public Customer(Long id, String name, String email, String cpf, LocalDate birthDate) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.cpf = cpf;
    this.birthDate = birthDate;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  protected void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  protected void setEmail(String email) {
    this.email = email;
  }

  public String getCpf() {
    return cpf;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  protected void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public void removeAccount(Account account) {
    accounts.remove(account);
  }

}
