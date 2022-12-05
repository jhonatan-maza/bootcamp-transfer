package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Transfer;
import com.nttdata.bootcamp.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

//Service implementation
@Service
public class TransferServiceImpl implements TransferService {
    @Autowired
    private TransferRepository transferRepository;

    @Override
    public Flux<Transfer> findAll() {
        Flux<Transfer> transactions = transferRepository.findAll();
        return transactions;
    }

    @Override
    public Flux<Transfer> findByAccountNumber(String accountNumber) {
        Flux<Transfer> transactions = transferRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return transactions;
    }

    @Override
    public Mono<Transfer> findByNumber(String Number) {
        Mono<Transfer> transaction = transferRepository
                .findAll()
                .filter(x -> x.getTransferNumber().equals(Number))
                .next();
        return transaction;
    }

    public Mono<Transfer> saveTransfer(Transfer dataTransfer, String typeTransfer ) {
        dataTransfer.setStatus("active");

        if(typeTransfer.equals("deposit")){
            dataTransfer.setDeposit(true);
            dataTransfer.setWithdraw(false);
        }
        else if(typeTransfer.equals("withdraw")){
            dataTransfer.setDeposit(false);
            dataTransfer.setWithdraw(true);
        }

        return transferRepository.save(dataTransfer);

    }

    @Override
    public Mono<Transfer> updateTransfer(Transfer dataTransfer) {

        Mono<Transfer> transactionMono = findByNumber(dataTransfer.getTransferNumber());
        try {
            dataTransfer.setDni(transactionMono.block().getDni());
            dataTransfer.setAmount(transactionMono.block().getAmount());
            dataTransfer.setCreationDate(transactionMono.block().getCreationDate());
            return transferRepository.save(dataTransfer);
        }catch (Exception e){
            return Mono.<Transfer>error(new Error("La transaccion " + dataTransfer.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteTransfer(String Number) {
        Mono<Transfer> transactionMono = findByNumber(Number);
        try {
            Transfer transfer = transactionMono.block();
            return transferRepository.delete(transfer);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("La transaccion numero" + Number+ " NO EXISTE"));
        }
    }



}
