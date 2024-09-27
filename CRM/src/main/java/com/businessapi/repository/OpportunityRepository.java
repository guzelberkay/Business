package com.businessapi.repository;

import com.businessapi.entity.Opportunity;
import com.businessapi.utility.enums.EStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findAllByNameContainingIgnoreCaseAndStatusAndMemberIdOrderByNameAsc(String s, EStatus eStatus, Long memberId, PageRequest of);
}
