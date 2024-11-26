package org.juwont.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.juwont.component.EventFactory;
import org.juwont.domain.CloseAccountEventData;
import org.juwont.domain.CreateAccountEventData;
import org.juwont.domain.Event;
import org.juwont.domain.EventType;
import org.juwont.domain.FundBalanceEventData;
import org.juwont.domain.TransactionEventData;
import org.juwont.repository.AccountRepository;
import org.juwont.service.dto.AccountAggregate;
import org.juwont.service.exception.InvalidFundAmountException;
import org.juwont.web.dto.FundBalanceDTO;
import org.juwont.web.dto.TransactionDTO;
import org.juwont.service.exception.AccountInArrearsException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    public static final UUID ID = UUID.randomUUID();
    @Mock
    private EventFactory eventFactory;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void createAccount_createsNewAccount() throws JsonProcessingException {
        final CreateAccountEventData createAccountEventData = CreateAccountEventData.builder()
                .id(ID)
                .timestamp(Instant.now())
                .build();
        Event createAccountEvent = buildEvent(getData(createAccountEventData), EventType.CREATE_ACCOUNT, 1);

        when(eventFactory.createAccountEvent(any())).thenReturn(createAccountEvent);

        AccountAggregate result = accountService.createAccount();

        verify(accountRepository).insertEvent(createAccountEvent);
        assertThat(result).isNotNull();
    }

    private <T> String getData(final T eventData) throws JsonProcessingException {
        return MAPPER
                .registerModule(new JavaTimeModule())
                .writeValueAsString(eventData);
    }

    private Event buildEvent(String data, EventType eventType, long version) {
        return Event.builder()
                .aggregateId(ID)
                .type(eventType)
                .data(data)
                .timestamp(Instant.now())
                .version(version)
                .build();
    }

    @Test
    void createTransaction_createsTransactionEvent() throws JsonProcessingException {
        TransactionDTO transactionDTO = new TransactionDTO(ID, "recipient", BigDecimal.valueOf(100));

        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setVersion(1);
        existingAccount.setId(ID);
        existingAccount.setAmountDue(BigDecimal.ZERO);

        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        TransactionEventData eventData = TransactionEventData.builder()
                .recipient("recipient")
                .amount(BigDecimal.valueOf(100))
                .timestamp(Instant.now())
                .build();
        Event transactionEvent = buildEvent(getData(eventData), EventType.TRANSACTION, 2);
        when(eventFactory.createTransactionEvent(2, transactionDTO)).thenReturn(transactionEvent);

        AccountAggregate result = accountService.createTransaction(transactionDTO);

        verify(accountRepository).insertEvent(transactionEvent);
        assertThat(result.getAmountDue())
                .isEqualTo(BigDecimal.valueOf(-100).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void fundBalance_updatesBalanceSuccessfully() throws JsonProcessingException {
        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setVersion(2);
        existingAccount.setId(ID);
        existingAccount.setAmountDue(BigDecimal.valueOf(-250));

        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        FundBalanceDTO fundBalanceDTO = new FundBalanceDTO(ID, BigDecimal.valueOf(100));
        FundBalanceEventData eventData = FundBalanceEventData.builder()
                .amount(BigDecimal.valueOf(100))
                .timestamp(Instant.now())
                .build();
        Event fundBalanceEvent = buildEvent(getData(eventData), EventType.FUND_ACCOUNT, 2);
        when(eventFactory.createfundBalanceEvent(3, fundBalanceDTO)).thenReturn(fundBalanceEvent);

        AccountAggregate result = accountService.fundBalance(fundBalanceDTO);

        verify(accountRepository).insertEvent(fundBalanceEvent);
        assertThat(result.getAmountDue())
                .isEqualTo(BigDecimal.valueOf(-150).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void fundBalance_throwsInvalidFundAmountException_whenBalanceIsPositive() throws JsonProcessingException {
        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setVersion(2);
        existingAccount.setId(ID);
        existingAccount.setAmountDue(BigDecimal.valueOf(50));

        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        FundBalanceDTO fundBalanceDTO = new FundBalanceDTO(ID, BigDecimal.valueOf(100));

        assertThatThrownBy(() -> accountService.fundBalance(fundBalanceDTO))
                .isInstanceOf(InvalidFundAmountException.class);
    }

    @Test
    void getAggregate_returnsCorrectAccountAggregate() {
        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setId(ID);
        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        AccountAggregate result = accountService.getAggregate(ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID);
    }

    @Test
    void closeAccount_closesAccountSuccessfully() throws JsonProcessingException {
        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setId(ID);
        existingAccount.setVersion(1);

        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        CloseAccountEventData eventData = CloseAccountEventData.builder()
                .timestamp(Instant.now())
                .build();
        Event closeAccountEvent = buildEvent(getData(eventData), EventType.CLOSE_ACCOUNT, 2);

        when(eventFactory.closeAccountEvent(2, ID)).thenReturn(closeAccountEvent);

        AccountAggregate result = accountService.closeAccount(ID);

        verify(accountRepository).insertEvent(closeAccountEvent);
        assertThat(result.isClosed()).isTrue();
    }

    @Test
    void closeAccount_throwsAccountInArrearsException_whenAmountDueNotZero() {
        AccountAggregate existingAccount = new AccountAggregate();
        existingAccount.setId(ID);
        existingAccount.setVersion(1);
        existingAccount.setAmountDue(BigDecimal.valueOf(250));

        when(accountRepository.findAccount(ID)).thenReturn(existingAccount);

        assertThatThrownBy(() -> accountService.closeAccount(ID))
                .isInstanceOf(AccountInArrearsException.class);
    }
}
