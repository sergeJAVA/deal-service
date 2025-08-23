package com.internship.deal_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.deal_service.DealServiceApplication;
import com.internship.deal_service.model.dto.DealTypeDto;
import com.internship.deal_service.testcontainer.TestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DealServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DealTypeCachingTest extends TestContainer {

    @Autowired
    @Qualifier("dealMetaDataCacheManager")
    private CacheManager cacheManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCachingDealType() throws Exception {

        assertThat(cacheManager.getCache("dealTypes").get("all")).isNull();

        mockMvc.perform(get("/deal-type/all"))
                .andExpect(status().isOk());
        assertThat(cacheManager.getCache("dealTypes").get("all")).isNotNull();

        DealTypeDto dealTypeDto = new DealTypeDto("TEST","Кэш тест");

        mockMvc.perform(put("/deal-type/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealTypeDto)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id").value(dealTypeDto.getId()))
                .andExpect(jsonPath("$.name").value(dealTypeDto.getName()));
        assertThat(cacheManager.getCache("dealTypes").get("all")).isNull();
    }
}
