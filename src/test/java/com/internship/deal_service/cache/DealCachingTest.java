package com.internship.deal_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.deal_service.DealServiceApplication;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.dto.DealTypeDto;
import com.internship.deal_service.repository.DealRepository;
import com.internship.deal_service.testcontainer.TestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DealServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DealCachingTest extends TestContainer {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCachingDeal() throws Exception {
        DealTypeDto dealTypeDto = new DealTypeDto();
        dealTypeDto.setId("CREDIT");

        DealRequest request = new DealRequest();
        request.setDescription("Кэш сделка");
        request.setAgreementNumber("1234-12345");
        request.setAgreementDate(LocalDate.now());
        request.setAgreementStartDt(LocalDateTime.now());
        request.setAvailabilityDate(LocalDate.now());
        request.setType(dealTypeDto);

        // Инвалидация кэша при сохранении/изменении, кэширование при GET запросе
        mockMvc.perform(post("/deal/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Deal deal = dealRepository.findAll().getFirst();
        assertThat(cacheManager.getCache("deals").get(deal.getId().toString())).isNull();

        mockMvc.perform(get("/deal/{id}", deal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(deal.getId().toString()))
                .andExpect(jsonPath("$.description").value("Кэш сделка"));
        assertThat(cacheManager.getCache("deals").get(deal.getId().toString())).isNotNull();

        request.setId(deal.getId());
        mockMvc.perform(post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        assertThat(cacheManager.getCache("deals").get(deal.getId().toString())).isNull();

        // Инвалидация кэша при изменении статуса
        mockMvc.perform(get("/deal/{id}", deal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(deal.getId().toString()))
                .andExpect(jsonPath("$.description").value("Кэш сделка"));
        assertThat(cacheManager.getCache("deals").get(deal.getId().toString())).isNotNull();

        DealStatusUpdateRequest statusUpdate = new DealStatusUpdateRequest(deal.getId(), "ACTIVE");

        mockMvc.perform(patch("/deal/change/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deal.getId().toString()))
                .andExpect(jsonPath("$.status.id").value("ACTIVE"));
        assertThat(cacheManager.getCache("deals").get(deal.getId().toString())).isNull();
    }
}
