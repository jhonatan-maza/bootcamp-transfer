package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Transfer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transfer")
public class TransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransferController.class);
	@Autowired
	private TransferService transferService;


	//Transactions search
	@GetMapping("/")
	public Flux<Transfer> findAllTransfer() {
		Flux<Transfer> transactions = transferService.findAll();
		LOGGER.info("Registered transfer: " + transactions);
		return transactions;
	}

	//Transactions by AccountNumber
	@GetMapping("/findAllTransferByNumber/{accountNumber}")
	public Flux<Transfer> findAllTransferByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Transfer> transactions = transferService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered transfer of account number: "+accountNumber +"-" + transactions);
		return transactions;
	}

	//Transaction  by transactionNumber
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@GetMapping("/findByTransferNumber/{numberTransfer}")
	public Mono<Transfer> findByTransferNumber(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Searching transfer by numberTransaction: " + numberTransaction);
		return transferService.findByNumber(numberTransaction);
	}

	//Save transaction
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@PostMapping(value = "/saveTransfer")
	public Mono<Transfer> saveTransfer(@RequestBody Transfer dataTransfer, @PathVariable("typeTransfer") String typeTransfer){
		Mono.just(dataTransfer).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransfer).onErrorResume(e -> Mono.just(dataTransfer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transfer> transactionMono = transferService.saveTransfer(dataTransfer,typeTransfer);
		return transactionMono;
	}

	//Update active
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@PutMapping("/updateTransafer/{numberTransfer}")
	public Mono<Transfer> updateTransfer(@PathVariable("numberTransfer") String numberTransfer,
											@Valid @RequestBody Transfer dataTransfer) {
		Mono.just(dataTransfer).doOnNext(t -> {

					t.setTransferNumber(numberTransfer);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransfer).onErrorResume(e -> Mono.just(dataTransfer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transfer> updateTransfer = transferService.updateTransfer(dataTransfer);
		return updateTransfer;
	}


	//Delete customer
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@DeleteMapping("/deleteTransfer/{numberTransaction}")
	public Mono<Void> deleteTransfer(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting transaction by numberTransaction: " + numberTransaction);
		Mono<Void> delete = transferService.deleteTransfer(numberTransaction);
		return delete;

	}

	private Mono<Transfer> fallBackGetTransfer(Exception e){
		Transfer activeStaff= new Transfer();
		Mono<Transfer> staffMono= Mono.just(activeStaff);
		return staffMono;
	}



}
