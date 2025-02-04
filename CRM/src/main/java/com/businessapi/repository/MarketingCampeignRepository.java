package com.businessapi.repository;

import com.businessapi.entity.MarketingCampaign;
import com.businessapi.utility.enums.EStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketingCampeignRepository extends JpaRepository<MarketingCampaign, Long> {
    List<MarketingCampaign> findAllByNameContainingIgnoreCaseAndStatusAndMemberIdOrderByNameAsc(String s, EStatus eStatus, Long memberId, PageRequest of);
}
