package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Transfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface TransferService {

    public Flux<Transfer> findAll();
    public Flux<Transfer> findByAccountNumber(String accountNumber);

    public Mono<Transfer> findByNumber(String number);
    public Mono<Transfer> saveTransfer(Transfer active, String typeTransfer);
    public Mono<Transfer> updateTransfer(Transfer dataActive);
    public Mono<Void> deleteTransfer(String accountNumber);




}
