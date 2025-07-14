package com.internship.deal_service.controller;

import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.Pagination;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.security.TokenAuthentication;
import com.internship.deal_service.model.security.TokenData;
import com.internship.deal_service.service.DealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ui/deal")
@RequiredArgsConstructor
@Slf4j
public class UIDealController {

    private final DealService dealService;

    @GetMapping("/{id}")
    public ResponseEntity<DealDto> getDealById(@PathVariable UUID id) {
        DealDto deal = dealService.getDealById(id);
        return ResponseEntity.ok(deal); // Возвращаем 200 OK с DTO сделки
    }

    @PostMapping("/save")
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Page<DealDto> resultPage = null;
        log.info("{}", authentication);
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();

        List<String> authenticatedUserRoles = tokenData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if (authenticatedUserRoles.contains("ROLE_SUPERUSER") ||
                authenticatedUserRoles.contains("ROLE_DEAL_SUPERUSER")) {

            resultPage = dealService.searchDeals(request, new Pagination(page, size));

        } else {
            if (authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER") &&
                authenticatedUserRoles.contains("ROLE_CREDIT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("OVERDRAFT", "CREDIT"))
                        .build();

                resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
            } else if (authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER") &&
                    !authenticatedUserRoles.contains("ROLE_CREDIT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("OVERDRAFT"))
                        .build();

                resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
            } else if (authenticatedUserRoles.contains("ROLE_CREDIT_USER") &&
                    !authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("CREDIT"))
                        .build();

                resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
            }
        }

        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/search/export")
    public ResponseEntity<byte[]> exportDeals(@RequestBody DealSearchRequest searchRequest,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              Authentication authentication) {

        byte[] excelBytes = null;

        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();

        List<String> authenticatedUserRoles = tokenData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (authenticatedUserRoles.contains("ROLE_SUPERUSER") ||
                authenticatedUserRoles.contains("ROLE_DEAL_SUPERUSER")) {

            excelBytes = dealService.exportDealsToExcel(searchRequest, new Pagination(page, size));

        } else {
            if (authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER") &&
                    authenticatedUserRoles.contains("CREDIT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("OVERDRAFT", "CREDIT"))
                        .build();

                excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
            } else if (authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER") &&
                    !authenticatedUserRoles.contains("ROLE_CREDIT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("OVERDRAFT"))
                        .build();

                excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
            } else if (authenticatedUserRoles.contains("ROLE_CREDIT_USER") &&
                    !authenticatedUserRoles.contains("ROLE_OVERDRAFT_USER")) {

                DealSearchRequest filterRequest = DealSearchRequest.builder()
                        .typeIds(List.of("CREDIT"))
                        .build();

                excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
            }
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "deals_export.xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}
