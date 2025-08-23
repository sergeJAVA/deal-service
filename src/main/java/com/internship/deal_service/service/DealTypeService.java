package com.internship.deal_service.service;

import com.internship.deal_service.model.dto.DealTypeDto;

import java.util.List;

public interface DealTypeService {

    void deleteById(String id);

    List<DealTypeDto> findAll();

    DealTypeDto create(String id, String name);

    DealTypeDto findById(String id);

}
