package com.example.deal_service.controller;

import com.example.deal_service.model.ContractorRoleRequest;
import com.example.deal_service.model.dto.ContractorRoleDto;
import com.example.deal_service.service.ContractorRoleService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractorRoleController.class)
class ContractorRoleControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContractorRoleService contractorRoleService;

    @Test
    void addRoleToContractor_shouldReturnAddedRoleDto() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "BORROWER";

        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        ContractorRoleDto responseDto = ContractorRoleDto.builder()
                .id(roleId)
                .name("Заемщик")
                .category("BORROWER")
                .build();

        when(contractorRoleService.addRoleToContractor(any(ContractorRoleRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId))
                .andExpect(jsonPath("$.name").value("Заемщик"))
                .andExpect(jsonPath("$.category").value(roleId));
    }

    @Test
    void deleteRoleFromContractor_shouldReturnNoContent() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "WARRANTY";

        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        doNothing().when(contractorRoleService).deleteRoleFromContractor(any(ContractorRoleRequest.class));

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

}