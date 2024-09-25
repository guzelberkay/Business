package com.businessapi.repositories;


import com.businessapi.entities.Customer;
import com.businessapi.entities.ProductCategory;
import com.businessapi.entities.enums.EStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    List<Customer> findAllByNameContainingIgnoreCaseAndStatusIsNotOrderByNameAsc(String s, EStatus status, PageRequest of);
    Optional<Customer> findCustomerByEmailIgnoreCase(String email);
}
