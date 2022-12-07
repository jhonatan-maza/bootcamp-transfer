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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/transfer")
public class TransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransferController.class);
	@Autowired
	private TransferService transferService;


	//Transfer search
	@GetMapping("/")
	public Flux<Transfer> findAllTransfer() {
		Flux<Transfer> transferFlux = transferService.findAll();
		LOGGER.info("Registered transfer: " + transferFlux);
		return transferFlux;
	}

	//Transfer by AccountNumber
	@GetMapping("/findAllTransferByNumber/{accountNumber}")
	public Flux<Transfer> findAllTransferByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Transfer> transferFlux = transferService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered transfer of account number: "+accountNumber +"-" + transferFlux);
		return transferFlux;
	}

	//Transfer  by transactionNumber
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@GetMapping("/findByTransferNumber/{numberTransfer}")
	public Mono<Transfer> findByTransferNumber(@PathVariable("numberTransfer") String numberTransfer) {
		LOGGER.info("Searching transfer by numberTransaction: " + numberTransfer);
		return transferService.findByNumber(numberTransfer);
	}

	//Save Transfer
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@PostMapping(value = "/saveTransfer")
	public Mono<Transfer> saveTransfer(@RequestBody Transfer dataTransfer){
		Mono.just(dataTransfer).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransfer).onErrorResume(e -> Mono.just(dataTransfer))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transfer> transactionMono = transferService.saveTransfer(dataTransfer);
		return transactionMono;
	}

	//Update Transfer
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@PutMapping("/updateTransfer/{numberTransfer}")
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


	//Delete Transfer
	@CircuitBreaker(name = "transfer", fallbackMethod = "fallBackGetTransfer")
	@DeleteMapping("/deleteTransfer/{numberTransaction}")
	public Mono<Void> deleteTransfer(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting Transfer by numberTransfer: " + numberTransaction);
		Mono<Void> delete = transferService.deleteTransfer(numberTransaction);
		return delete;

	}

	private Mono<Transfer> fallBackGetTransfer(Exception e){
		Transfer transfer= new Transfer();
		Mono<Transfer> transferMono= Mono.just(transfer);
		return transferMono;
	}




}
