package com.internship.deal_service.controller;

import com.internship.deal_service.model.dto.DealStatusDto;
import com.internship.deal_service.service.DealStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal-status")
@RequiredArgsConstructor
public class DealStatusController {

    private final DealStatusService dealStatusService;

    @GetMapping("/all")
    public ResponseEntity<List<DealStatusDto>> findAll() {
        return ResponseEntity.ok(dealStatusService.findAll());
    }

    @PutMapping("/save")
    public ResponseEntity<DealStatusDto> saveDealStatus(@RequestBody DealStatusDto dealStatusDto) {
        DealStatusDto saved = dealStatusService.create(dealStatusDto.getId(), dealStatusDto.getName());
        if (saved != null) {
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        dealStatusService.deleteById(id);
        return ResponseEntity.ok("DealStatus with id <<" + id + ">> has deleted.");
    }

}
