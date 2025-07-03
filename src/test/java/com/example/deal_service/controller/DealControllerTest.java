package com.example.deal_service.controller;

import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSearchRequest;
import com.example.deal_service.model.DealStatusUpdateRequest;
import com.example.deal_service.model.Pagination;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.model.dto.DealStatusDto;
import com.example.deal_service.service.DealService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DealService dealService;

    @Test
    void getDealById_shouldReturnDealDto_whenDealExists() throws Exception {
        UUID dealId = UUID.randomUUID();
        DealDto dealDto = DealDto.builder().id(dealId).description("Тестовая сделка").build();

        when(dealService.getDealById(dealId)).thenReturn(dealDto);

        mockMvc.perform(get("/deal/{id}", dealId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dealId.toString()))
                .andExpect(jsonPath("$.description").value("Тестовая сделка"));
    }

    @Test
    void saveDeal_shouldReturnSavedDealDto() throws Exception {
        DealRequest request = DealRequest.builder().description("Новая сделка").build();
        DealDto savedDealDto = DealDto.builder().id(UUID.randomUUID()).description("Новая сделка").build();

        when(dealService.saveDeal(any(DealRequest.class))).thenReturn(savedDealDto);

        mockMvc.perform(post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Новая сделка"));
    }

    @Test
    void changeDealStatus_shouldReturnUpdatedDealDto() throws Exception {
        UUID dealId = UUID.randomUUID();

        String newStatusId = "CLOSED";

        DealStatusDto statusDto = DealStatusDto.builder()
                .name("Закрытая")
                .id(newStatusId)
                .build();

        DealStatusUpdateRequest request = DealStatusUpdateRequest.builder()
                .dealId(dealId)
                .newStatusId(newStatusId)
                .build();

        DealDto updatedDealDto = DealDto.builder().id(dealId).status(statusDto).build();

        when(dealService.changeDealStatus(any(UUID.class), any(DealStatusUpdateRequest.class))).thenReturn(updatedDealDto);

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dealId.toString()))
                .andExpect(jsonPath("$.status.id").value(newStatusId));
    }

    @Test
    void searchDeals_shouldReturnPageOfDeals() throws Exception {
        DealSearchRequest request = new DealSearchRequest();
        Pageable pageable = PageRequest.of(0, 10);
        DealDto dealDto = DealDto.builder().id(UUID.randomUUID()).build();
        Page<DealDto> dealPage = new PageImpl<>(Collections.singletonList(dealDto), pageable, 1);

        when(dealService.searchDeals(any(DealSearchRequest.class), any(Pageable.class))).thenReturn(dealPage);

        mockMvc.perform(post("/deal/search?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void exportDeals_shouldReturnExcelFile() throws Exception {
        DealSearchRequest request = new DealSearchRequest();
        byte[] excelBytes = "Фиктивное содержание excel".getBytes();

        when(dealService.exportDealsToExcel(any(DealSearchRequest.class), any(Pagination.class))).thenReturn(excelBytes);

        mockMvc.perform(post("/deal/search/export?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"deals_export.xlsx\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(excelBytes));
    }

}