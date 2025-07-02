package com.example.deal_service.service;

import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSearchRequest;
import com.example.deal_service.model.DealStatusUpdateRequest;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.model.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DealService {

    DealDto getDealById(UUID id);

    DealDto saveDeal(DealRequest request);

    DealDto changeDealStatus(UUID dealId, DealStatusUpdateRequest request);

    Page<DealDto> searchDeals(DealSearchRequest request, Pageable pageable);

    byte[] exportDealsToExcel(DealSearchRequest searchRequest, Pagination pagination);

}
