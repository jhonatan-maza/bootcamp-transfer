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
                .filter(x -> x.getAccountNumberOrigin().equals(accountNumber));
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

    public Mono<Transfer> saveTransfer(Transfer dataTransfer ) {
        dataTransfer.setStatus("active");
        return transferRepository.save(dataTransfer);

    }

    @Override
    public Mono<Transfer> updateTransfer(Transfer dataTransfer) {

        Mono<Transfer> transactionMono = findByNumber(dataTransfer.getTransferNumber());
        try {
            dataTransfer.setDniOrigin(transactionMono.block().getDniOrigin());
            dataTransfer.setDniDestination(transactionMono.block().getDniDestination());
            dataTransfer.setAccountNumberOrigin(transactionMono.block().getAccountNumberOrigin());
            dataTransfer.setAccountNumberDestination(transactionMono.block().getAccountNumberDestination());
            dataTransfer.setAmount(transactionMono.block().getAmount());
            dataTransfer.setCreationDate(transactionMono.block().getCreationDate());
            return transferRepository.save(dataTransfer);
        }catch (Exception e){
            return Mono.<Transfer>error(new Error("The transfer " + dataTransfer.getTransferNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteTransfer(String Number) {
        Mono<Transfer> transferMono = findByNumber(Number);
        try {
            Transfer transfer = transferMono.block();
            return transferRepository.delete(transfer);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The transfer number" + Number+ " do not exists"));
        }
    }


}
