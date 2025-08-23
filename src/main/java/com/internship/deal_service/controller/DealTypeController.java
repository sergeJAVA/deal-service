package com.internship.deal_service.controller;

import com.internship.deal_service.model.dto.DealTypeDto;
import com.internship.deal_service.service.DealTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal-type")
@RequiredArgsConstructor
public class DealTypeController {

    private final DealTypeService dealTypeService;

    @GetMapping("/all")
    public ResponseEntity<List<DealTypeDto>> findAll() {
        return ResponseEntity.ok(dealTypeService.findAll());
    }

    @PutMapping("/save")
    public ResponseEntity<DealTypeDto> save(@RequestBody DealTypeDto dealTypeDto) {
        DealTypeDto saved = dealTypeService.create(dealTypeDto.getId(), dealTypeDto.getName());
        if (saved != null) {
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
