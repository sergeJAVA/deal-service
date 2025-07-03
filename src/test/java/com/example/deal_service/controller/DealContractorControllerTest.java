package com.example.deal_service.controller;


import com.example.deal_service.model.DealContractorRequest;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.service.DealContractorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealContractorController.class)
class DealContractorControllerTest {
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
}
