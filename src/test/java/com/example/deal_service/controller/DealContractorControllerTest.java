package com.example.deal_service.controller;


import com.example.deal_service.testcontainer.TestContainer;
import com.internship.deal_service.DealServiceApplication;
import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.service.DealContractorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DealServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DealContractorControllerTest extends TestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DealContractorService dealContractorService;

    @Test
    void saveDealContractor_shouldReturnSavedDto() throws Exception {
        UUID dealId = UUID.randomUUID();
        DealContractorRequest request = DealContractorRequest.builder()
                .dealId(dealId)
                .name("ООО Ромашка")
                .inn("1234567890")
                .build();

        DealContractorDto savedDto = DealContractorDto.builder()
                .id(UUID.randomUUID())
                .name("ООО Ромашка")
                .build();

        when(dealContractorService.saveDealContractor(any(DealContractorRequest.class))).thenReturn(savedDto);

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("ООО Ромашка"));
    }

    @Test
    void deleteDealContractor_shouldReturnNoContent() throws Exception {
        UUID dealContractorId = UUID.randomUUID();

        doNothing().when(dealContractorService).deleteDealContractor(dealContractorId);

        mockMvc.perform(delete("/deal-contractor/delete")
                        .param("id", dealContractorId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveDealContractor_whenContractorNotFound_shouldReturnNotFound() throws Exception {
        UUID dealId = UUID.randomUUID();
        UUID contractorId = UUID.randomUUID();
        DealContractorRequest request = DealContractorRequest.builder()
                .dealId(dealId)
                .id(contractorId)
                .name("Неизвестный")
                .build();

        when(dealContractorService.saveDealContractor(any()))
                .thenThrow(new DealContractorException("Не найден"));

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveDealContractor_whenDealNotFound_shouldReturnNotFound() throws Exception {
        DealContractorRequest request = DealContractorRequest.builder()
                .dealId(UUID.randomUUID())
                .name("Тест")
                .build();

        when(dealContractorService.saveDealContractor(any()))
                .thenThrow(new DealContractorException("Сделка не найдена"));

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveDealContractor_withInvalidJson_shouldReturnBadRequest() throws Exception {
        String invalidJson = "{ \"dealId\": \"неправильныйUUID\" }";

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDealContractor_whenNotFound_shouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new DealContractorException("Не найден")).when(dealContractorService).deleteDealContractor(id);

        mockMvc.perform(delete("/deal-contractor/delete")
                        .param("id", id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDealContractor_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/deal-contractor/delete"))
                .andExpect(status().isBadRequest());
    }

}
