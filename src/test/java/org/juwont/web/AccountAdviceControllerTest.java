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
class AccountAdviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService service;

    @Test
    void user_receives_error_when_account_does_not_exist() throws Exception {
        final UUID id = UUID.randomUUID();
        performRequest(HttpMethod.GET, "/api/account/%s".formatted(id))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                          "errorMessage" : "Account %s was not found"
                        }
                        """.formatted(id)
                ));
    }


    @Test
    void user_receives_error_when_fund_amount_is_more_than_what_is_due() throws Exception {
        final AccountAggregate account = service.createAccount();

        final FundBalanceDTO fundBalanceDTO = FundBalanceDTO.builder()
                .accountId(account.getId())
                .amount(BigDecimal.valueOf(200))
                .build();

        performRequest(HttpMethod.PUT, "/api/account/fund", objectMapper.writeValueAsString(fundBalanceDTO))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMessage":"Fund amount can not be more than what is due"
                        }
                        """
                ));
    }


    @Test
    void user_can_close_account() throws Exception {
        final AccountAggregate account = service.createAccount();

        final TransactionDTO transaction = TransactionDTO.builder()
                .accountId(account.getId())
                .recipient("recipient")
                .amount(BigDecimal.valueOf(100))
                .build();

        service.createTransaction(transaction);

        performRequest(HttpMethod.PUT, "/api/account/%s/close".formatted(account.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "errorMessage":"Account %s is still due to pay -100.00"
                        }
                        """.formatted(account.getId())
                ));
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