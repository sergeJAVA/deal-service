package com.internship.deal_service.controller;

import com.internship.deal_service.testcontainer.TestContainer;
import com.internship.deal_service.DealServiceApplication;
import com.internship.deal_service.exception.ContractorRoleException;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.model.dto.ContractorRoleDto;
import com.internship.deal_service.service.ContractorRoleService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DealServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContractorRoleControllerTest extends TestContainer {


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

    @Test
    void addRoleToContractor_shouldReturnNotFound_whenDealContractorNotFound() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "BORROWER";
        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        when(contractorRoleService.addRoleToContractor(any()))
                .thenThrow(new ContractorRoleException("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен."));

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен."));
    }

    @Test
    void addRoleToContractor_shouldReturnNotFound_whenRoleNotFound() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "UNKNOWN_ROLE";
        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        when(contractorRoleService.addRoleToContractor(any()))
                .thenThrow(new ContractorRoleException("ContractorRole с id <<" + roleId + ">> не найден или неактивен."));

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ContractorRole с id <<" + roleId + ">> не найден или неактивен."));
    }

    @Test
    void deleteRoleFromContractor_shouldReturnNotFound_whenContractorNotFound() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "WARRANTY";
        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        doThrow(new ContractorRoleException("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен."))
                .when(contractorRoleService).deleteRoleFromContractor(any());

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("DealContractor c id <<" + dealContractorId + ">> не найден или неактивен."));
    }

    @Test
    void deleteRoleFromContractor_shouldReturnNotFound_whenRoleNotFound() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "INVALID_ROLE";
        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        doThrow(new ContractorRoleException("ContractorRole с id <<" + roleId + ">> не найдена ли неактивна."))
                .when(contractorRoleService).deleteRoleFromContractor(any());

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ContractorRole с id <<" + roleId + ">> не найдена ли неактивна."));

    }

    @Test
    void deleteRoleFromContractor_shouldReturnNotFound_whenLinkNotFound() throws Exception {
        UUID dealContractorId = UUID.randomUUID();
        String roleId = "BORROWER";
        ContractorRoleRequest request = new ContractorRoleRequest(dealContractorId, roleId);

        doThrow(new ContractorRoleException("Связь ContractorRole между контрагентом <<" + dealContractorId + ">> и ролью " + roleId + " не найдена."))
                .when(contractorRoleService).deleteRoleFromContractor(any());

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Связь ContractorRole между контрагентом <<" + dealContractorId + ">> и ролью " + roleId + " не найдена."));

    }

}