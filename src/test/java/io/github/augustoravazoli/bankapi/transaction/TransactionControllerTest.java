package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.Matchers.endsWith;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import io.github.augustoravazoli.bankapi.ControllerTestTemplate;

@Import(TransactionMapperImpl.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest extends ControllerTestTemplate {

  @MockBean
  private TransactionService transactionService;

  @Test
  void whenCreateDepositTransaction_thenReturns201AndCreatedTransaction() throws Exception {
    // given
    var newTransaction = new TransactionRequest(BigDecimal.TEN, 1L, null);
    var savedTransaction = new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, null);
    var returnedTransaction = new TransactionResponse(
      1L, BigDecimal.TEN, TransactionType.DEPOSIT, LocalDate.now(), 1L, null
    );
    // and
    when(transactionService.createDepositTransaction(any(Transaction.class)))
      .thenReturn(savedTransaction);
    // when
    mvc.perform(post("/api/v1/transactions/deposits")
      .content(toJson(newTransaction))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/transactions/deposits/" + returnedTransaction.id())),
      content().json(toJson(returnedTransaction))
    )
    .andDo(document("transaction/create/deposit", depositAndWithdrawTransactionSnippet()));
  }

  @Test
  void whenCreateWithdrawalTransaction_thenReturns201AndCreatedTransaction() throws Exception {
    // given
    var newTransaction = new TransactionRequest(BigDecimal.TEN, 1L, null);
    var savedTransaction = new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, null);
    var returnedTransaction = new TransactionResponse(
      1L, BigDecimal.TEN, TransactionType.DEPOSIT, LocalDate.now(), 1L, null
    );
    // and
    when(transactionService.createWithdrawalTransaction(any(Transaction.class)))
      .thenReturn(savedTransaction);
    // when
    mvc.perform(post("/api/v1/transactions/withdrawals")
      .content(toJson(newTransaction))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/transactions/withdrawals/" + returnedTransaction.id())),
      content().json(toJson(returnedTransaction))
    )
    .andDo(document("transaction/create/withdraw", depositAndWithdrawTransactionSnippet()));
  }

  @Test
  void whenCreateTransferationTransaction_thenReturns201AndCreatedTransaction() throws Exception {
    // given
    var newTransaction = new TransactionRequest(BigDecimal.TEN, 1L, 2L);
    var savedTransaction = new Transaction(1L, BigDecimal.TEN, TransactionType.TRANSFERATION, 1L, 2L);
    var returnedTransaction = new TransactionResponse(
      1L, BigDecimal.TEN, TransactionType.TRANSFERATION, LocalDate.now(), 1L, 2L
    );
    // and
    when(transactionService.createTransferationTransaction(any(Transaction.class)))
      .thenReturn(savedTransaction);
    // when
    mvc.perform(post("/api/v1/transactions/transferations")
      .content(toJson(newTransaction))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/transactions/transferations/" + returnedTransaction.id())),
      content().json(toJson(returnedTransaction))
    )
    .andDo(document("transaction/create/transfer", transferTransactionSnippet()));
  }

  @Test
  void whenFindAllTransactions_thenReturns200AndFindedTransactions() throws Exception {
    // given
    var findedTransactions = List.of(
      new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, null),
      new Transaction(2L, BigDecimal.TEN, TransactionType.WITHDRAWAL, 2L, null),
      new Transaction(3L, BigDecimal.TEN, TransactionType.TRANSFERATION, 3L, 2L)
    );
    var returnedTransactions = List.of(
      new TransactionResponse(1L, BigDecimal.TEN, TransactionType.DEPOSIT, LocalDate.now(), 1L, null),
      new TransactionResponse(2L, BigDecimal.TEN, TransactionType.WITHDRAWAL, LocalDate.now(), 2L, null),
      new TransactionResponse(3L, BigDecimal.TEN, TransactionType.TRANSFERATION, LocalDate.now(), 3L, 2L)
    );
    // and
    when(transactionService.findAllTransactions(anyLong(), anyInt(), anyInt()))
      .thenReturn(findedTransactions);
    // when
    mvc.perform(
      get("/api/v1/transactions?account-id={account-id}&page={page}&size={size}", 3L, 0, 4)
    )
    // then
    .andExpectAll(
      status().isOk(),
      content().json(toJson(returnedTransactions))
    )
    .andDo(document("transaction/find-all", findAllTransactionsSnippet()));
  }

  private RequestFieldsSnippet depositAndWithdrawTransactionSnippet() {
    var fields = new ConstrainedFields(TransactionRequest.class);
    return requestFields(
      fields.path("amount").description("Transaction's amount"),
      fields.path("originAccountId").description("Transaction's origin account id")
    );
  }

  private RequestFieldsSnippet transferTransactionSnippet() {
    var fields = new ConstrainedFields(TransactionRequest.class);
    return requestFields(
      fields.path("amount").description("Transaction's amount"),
      fields.path("originAccountId").description("Transaction's origin account id"),
      fields.pathExcludingGroups("destinationAccountId", OnDepositOrWithdraw.class)
        .description("Transactions's destination account id")
    );
  }

  private QueryParametersSnippet findAllTransactionsSnippet() {
    return queryParameters(
      parameterWithName("account-id").description("The id of the account associated with this transaction"),
      parameterWithName("page").description("The page to retrieve"),
      parameterWithName("size").description("Entries per page")
    );
  }

}
