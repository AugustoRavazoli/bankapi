package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.hamcrest.Matchers.endsWith;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
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
    var savedTransaction = new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, 1L);
    var returnedTransaction = new TransactionResponse(
      1L, BigDecimal.TEN, TransactionType.DEPOSIT, LocalDate.now(), 1L, 1L
    );
    // and
    when(transactionService.createDepositTransaction(any(Transaction.class))).thenReturn(savedTransaction);
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
    var savedTransaction = new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, 1L);
    var returnedTransaction = new TransactionResponse(
      1L, BigDecimal.TEN, TransactionType.DEPOSIT, LocalDate.now(), 1L, 1L
    );
    // and
    when(transactionService.createWithdrawalTransaction(any(Transaction.class))).thenReturn(savedTransaction);
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
    when(transactionService.createTransferationTransaction(any(Transaction.class))).thenReturn(savedTransaction);
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

}
