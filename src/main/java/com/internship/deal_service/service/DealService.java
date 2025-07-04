package com.internship.deal_service.service;

import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.Pagination;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DealService {

    DealDto getDealById(UUID id);

    DealDto saveDeal(DealRequest request);

    DealDto changeDealStatus(UUID dealId, DealStatusUpdateRequest request);

    Page<DealDto> searchDeals(DealSearchRequest request, Pagination pagination);

    byte[] exportDealsToExcel(DealSearchRequest searchRequest, Pagination pagination);

}
