package com.example.deal_service.controller;

import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSearchRequest;
import com.example.deal_service.model.DealStatusUpdateRequest;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @GetMapping("/{id}")
    public ResponseEntity<DealDto> getDealById(@PathVariable UUID id) {
        DealDto deal = dealService.getDealById(id);
        return ResponseEntity.ok(deal); // Возвращаем 200 OK с DTO сделки
    }

    @PostMapping("/save") // Для сохранения/обновления
    public ResponseEntity<DealDto> saveDeal(@RequestBody DealRequest request) {
        DealDto savedDeal = dealService.saveDeal(request);
        return ResponseEntity.ok(savedDeal);
    }

    @PatchMapping("/change/status")
    public ResponseEntity<DealDto> changeDealStatus(@RequestBody DealStatusUpdateRequest request) {
        DealDto updatedDeal = dealService.changeDealStatus(request.getDealId(), request);
        return ResponseEntity.ok(updatedDeal);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<DealDto>> searchDeals(
            @RequestBody DealSearchRequest request,
            Pageable pageable) {
        Page<DealDto> resultPage = dealService.searchDeals(request, pageable);
        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/search/export")
    public ResponseEntity<byte[]> exportDeals(@RequestBody DealSearchRequest searchRequest) {
        byte[] excelBytes = dealService.exportDealsToExcel(searchRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "deals_export.xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
