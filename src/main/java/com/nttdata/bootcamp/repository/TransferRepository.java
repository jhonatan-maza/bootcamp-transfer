package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Transfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface TransferRepository extends ReactiveCrudRepository<Transfer, String> {
}
