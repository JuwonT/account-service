package org.juwont.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.juwont.service.AccountService;
import org.juwont.service.dto.AccountAggregate;
import org.juwont.web.dto.FundBalanceDTO;
import org.juwont.web.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping("/api/account/create")
    public ResponseEntity<AccountAggregate> createAccount() {
        return ResponseEntity.status(CREATED).body(service.createAccount());
    }

    @GetMapping("/api/account/{id}")
    public ResponseEntity<AccountAggregate> getAccount(@PathVariable final UUID id) {
        return ResponseEntity.ok(service.getAggregate(id));
    }

    @PutMapping("/api/account/transaction")
    public ResponseEntity<AccountAggregate> createTransaction(@RequestBody @Valid final TransactionDTO transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction));
    }


    @PutMapping("/api/account/fund")
    public ResponseEntity<AccountAggregate> fundBalance(@RequestBody @Valid final FundBalanceDTO fundBalance) {
        return ResponseEntity.ok(service.fundBalance(fundBalance));
    }

    @PutMapping("/api/account/{id}/close")
    public ResponseEntity<AccountAggregate> closeAccount(@PathVariable final UUID id) {
        return ResponseEntity.ok(service.closeAccount(id));
    }
}
