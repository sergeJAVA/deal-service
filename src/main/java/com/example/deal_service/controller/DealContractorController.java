package com.example.deal_service.controller;

import com.example.deal_service.model.DealContractorRequest;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.service.DealContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
public class DealContractorController {

    private final DealContractorService dealContractorService;

    @PutMapping("/save")
    public ResponseEntity<DealContractorDto> saveDealContractor(@RequestBody DealContractorRequest request) {
        DealContractorDto savedDto = dealContractorService.saveDealContractor(request);
        return ResponseEntity.ok(savedDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDealContractor(@RequestParam UUID id) {
        dealContractorService.deleteDealContractor(id);
        return ResponseEntity.noContent().build();
    }
}
