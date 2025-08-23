package com.internship.deal_service.service;

import com.internship.deal_service.model.dto.DealStatusDto;

import java.util.List;

public interface DealStatusService {

    void deleteById(String id);

    DealStatusDto findById(String id);

    DealStatusDto findByName(String name);

    DealStatusDto create(String id, String name);

    List<DealStatusDto> findAll();

}
