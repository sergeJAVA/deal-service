package com.internship.deal_service.controller;

import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.service.DealContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ui/deal-contractor")
@RequiredArgsConstructor
public class UIDealContractorController {

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
