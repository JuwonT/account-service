package org.juwont.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.juwont.service.AccountService;
import org.juwont.service.dto.AccountAggregate;
import org.juwont.web.dto.FundBalanceDTO;
import org.juwont.web.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService service;

    @Test
    void user_can_create_new_account() throws Exception {
        performRequest(HttpMethod.POST, "/api/account/create")
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                          "amountDue": 0,
                          "closedAt": null,
                          "lastFunded": null,
                          "version": 1,
                          "transactions": [],
                          "closed": false
                        }
                        """
                ))
                .andExpect(jsonPath("id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")));
    }

    @Test
    void user_can_retrieve_account() throws Exception {
        final AccountAggregate account = service.createAccount();

        performRequest(HttpMethod.GET, "/api/account/%s".formatted(account.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(account)));
    }

    @Test
    void user_can_add_transaction_to_balance() throws Exception {
        final AccountAggregate account = service.createAccount();

        final TransactionDTO transaction = TransactionDTO.builder()
                .accountId(account.getId())
                .recipient("recipient")
                .amount(BigDecimal.valueOf(100))
                .build();

        performRequest(HttpMethod.PUT, "/api/account/transaction", objectMapper.writeValueAsString(transaction))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "amountDue": -100.00,
                          "closedAt": null,
                          "lastFunded": null,
                          "version": 2,
                          "transactions": [
                            {
                              "recipient": "recipient",
                              "amount": 100.00
                            }
                          ],
                          "closed": false
                        }
                        """
                ))
                .andExpect(jsonPath("id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")));
    }

    @Test
    void user_can_add_fundsToAccount() throws Exception {
        final AccountAggregate account = service.createAccount();
        final TransactionDTO transaction = TransactionDTO.builder()
                .accountId(account.getId())
                .recipient("recipient")
                .amount(BigDecimal.valueOf(100))
                .build();

        service.createTransaction(transaction);

        FundBalanceDTO fundBalanceDTO = FundBalanceDTO.builder()
                .accountId(account.getId())
                .amount(BigDecimal.valueOf(100))
                .build();

        performRequest(HttpMethod.PUT, "/api/account/fund", objectMapper.writeValueAsString(fundBalanceDTO))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "amountDue": 0.00,
                          "closedAt": null,
                          "version": 3,
                          "transactions": [
                            {
                              "recipient": "recipient",
                              "amount": 100.00
                            }
                          ],
                          "closed": false
                        }
                        """
                ))
                .andExpect(jsonPath("id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")));
    }


    @Test
    void user_can_close_account() throws Exception {
        final AccountAggregate account = service.createAccount();

        performRequest(HttpMethod.PUT, "/api/account/%s/close".formatted(account.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "amountDue": 0.00,
                          "version": 2,
                          "transactions": [],
                          "closed": true
                        }
                        """
                ))
                .andExpect(jsonPath("id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")));
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path) throws Exception {
        return mockMvc.perform(
                request(method, path).contentType(APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path,
                                         final String content) throws Exception {
        return mockMvc.perform(
                request(method, path)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performRequest(final HttpMethod method,
                                         final String path,
                                         final UUID accountId) throws Exception {
        return mockMvc.perform(
                request(method, path, accountId)
                        .contentType(APPLICATION_JSON)
        ).andDo(print());
    }
}