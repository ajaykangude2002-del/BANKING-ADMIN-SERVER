package com.bank.repository;

import com.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // By extending JpaRepository, Spring automatically generates all the code 
    // for save(), findAll(), findById(), and deleteById()!
}