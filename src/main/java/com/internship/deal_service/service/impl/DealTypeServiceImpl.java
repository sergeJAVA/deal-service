package com.internship.deal_service.service.impl;

import com.internship.deal_service.model.DealType;
import com.internship.deal_service.model.dto.DealTypeDto;
import com.internship.deal_service.model.mapper.DealTypeMapper;
import com.internship.deal_service.repository.DealTypeRepository;
import com.internship.deal_service.service.DealTypeService;
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
public class DealTypeServiceImpl implements DealTypeService {

    private final DealTypeRepository dealTypeRepository;
    private static final String DEAL_TYPES_PREFIX = "dealTypes";

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = DEAL_TYPES_PREFIX, key = "#id", cacheManager = "dealMetaDataCacheManager"),
            @CacheEvict(value = DEAL_TYPES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager")
    })
    public void deleteById(String id) {
        dealTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DEAL_TYPES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager")
    public List<DealTypeDto> findAll() {
        List<DealType> dealTypes = dealTypeRepository.findAll();
        if (!dealTypes.isEmpty()) {
            return dealTypes.stream().map(DealTypeMapper::toDto).toList();
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    @CacheEvict(value = DEAL_TYPES_PREFIX, key = "'all'", cacheManager = "dealMetaDataCacheManager")
    public DealTypeDto create(String id, String name) {
        Optional<DealType> existing =  dealTypeRepository.findById(id);
        if (existing.isPresent()) {
            return null;
        }
        DealType dealType = new DealType();
        dealType.setName(name);
        dealType.setId(id);
        return DealTypeMapper.toDto(dealTypeRepository.save(dealType));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DEAL_TYPES_PREFIX, key = "#id", cacheManager = "dealMetaDataCacheManager")
    public DealTypeDto findById(String id) {
        Optional<DealType> dealType = dealTypeRepository.findById(id);
        return dealType.map(DealTypeMapper::toDto).orElse(null);
    }

}
