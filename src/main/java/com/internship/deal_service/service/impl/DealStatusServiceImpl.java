package com.internship.deal_service.service.impl;

import com.internship.deal_service.model.DealStatus;
import com.internship.deal_service.model.dto.DealStatusDto;
import com.internship.deal_service.model.mapper.DealStatusMapper;
import com.internship.deal_service.repository.DealStatusRepository;
import com.internship.deal_service.service.DealStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealStatusServiceImpl implements DealStatusService {

    private final DealStatusRepository dealStatusRepository;
    private static final String DEAL_STATUSES_PREFIX = "dealStatuses";

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = DEAL_STATUSES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager"),
            @CacheEvict(value = DEAL_STATUSES_PREFIX, key = "#id", cacheManager = "dealMetaDataCacheManager")
    })
    public void deleteById(String id) {
        dealStatusRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DEAL_STATUSES_PREFIX, key = "#id", cacheManager = "dealMetaDataCacheManager")
    public DealStatusDto findById(String id) {
        Optional<DealStatus> dealStatus = dealStatusRepository.findById(id);
        return dealStatus.map(DealStatusMapper::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DEAL_STATUSES_PREFIX, key = "#name", cacheManager = "dealMetaDataCacheManager")
    public DealStatusDto findByName(String name) {
        Optional<DealStatus> dealStatus = dealStatusRepository.findByName(name);
        return dealStatus.map(DealStatusMapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = DEAL_STATUSES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager")
    public DealStatusDto create(String id, String name) {
        Optional<DealStatus> existing = dealStatusRepository.findById(id);
        if (existing.isPresent()) {
            return null;
        }
        DealStatus dealStatus = new DealStatus();
        dealStatus.setId(id);
        dealStatus.setName(name);
        DealStatus saved = dealStatusRepository.save(dealStatus);
        return DealStatusMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DEAL_STATUSES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager")
    public List<DealStatusDto> findAll() {
        List<DealStatus> dealStatuses = dealStatusRepository.findAll();
        if (!dealStatuses.isEmpty()) {
            return dealStatuses.stream().map(DealStatusMapper::toDto).toList();
        }
        return Collections.emptyList();
    }

}
