package org.juwont.service;

import lombok.RequiredArgsConstructor;
import org.juwont.component.EventFactory;
import org.juwont.domain.Event;
import org.juwont.repository.AccountRepository;
import org.juwont.service.dto.AccountAggregate;
import org.juwont.service.exception.AccountInArrearsException;
import org.juwont.service.exception.InvalidFundAmountException;
import org.juwont.web.dto.FundBalanceDTO;
import org.juwont.web.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final EventFactory factory;
    private final AccountRepository repository;

    public AccountAggregate createAccount() {
        final UUID id = UUID.randomUUID();

        final Event createAccountEvent = factory.createAccountEvent(id);
        repository.insertEvent(createAccountEvent);

        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.apply(createAccountEvent);

        return accountAggregate;
    }

    public AccountAggregate createTransaction(final TransactionDTO transaction) {
        final AccountAggregate accountAggregate = repository.findAccount(transaction.accountId());

        final var transactionEvent = factory.createTransactionEvent(accountAggregate.getVersion() + 1, transaction);
        repository.insertEvent(transactionEvent);

        accountAggregate.apply(transactionEvent);
        return accountAggregate;
    }

    public AccountAggregate fundBalance(final FundBalanceDTO fundBalance) {
        final AccountAggregate accountAggregate = repository.findAccount(fundBalance.accountId());

        final BigDecimal updatedBalance = accountAggregate.getAmountDue().add(fundBalance.amount());
        if (BigDecimal.ZERO.compareTo(updatedBalance) < 0) throw new InvalidFundAmountException();

        final Event transactionEvent = factory.createfundBalanceEvent(accountAggregate.getVersion() + 1, fundBalance);
        repository.insertEvent(transactionEvent);

        accountAggregate.apply(transactionEvent);
        return accountAggregate;
    }

    public AccountAggregate getAggregate(final UUID id) {
        return repository.findAccount(id);
    }

    public AccountAggregate closeAccount(final UUID id) {
        final AccountAggregate accountAggregate = repository.findAccount(id);

        final BigDecimal amountDue = accountAggregate.getAmountDue();
        if (BigDecimal.ZERO.compareTo(amountDue) != 0) throw new AccountInArrearsException(id, amountDue);

        final Event closeAccountEvent = factory.closeAccountEvent(accountAggregate.getVersion() + 1, id);
        repository.insertEvent(closeAccountEvent);

        accountAggregate.apply(closeAccountEvent);

        return accountAggregate;
    }
}