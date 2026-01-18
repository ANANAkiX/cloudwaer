package com.cloudwaer.admin.serve.service;

import com.cloudwaer.admin.api.dto.DictItemDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Dictionary cache service.
 */
@Slf4j
@Service
public class DictCacheService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    /**
     * Redis key for all dictionaries (type -> items).
     */
    private static final String DICT_ALL_CACHE_KEY = "cloudwaer:dict:all";

    public void cacheAll(Map<String, List<DictItemDTO>> dictMap) {
        if (redisTemplate == null || objectMapper == null) {
            log.warn("Redis or ObjectMapper not available, skip dict cache.");
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(dictMap);
            redisTemplate.opsForValue().set(DICT_ALL_CACHE_KEY, json, 30, TimeUnit.DAYS);
            log.info("Dict cache updated, type count: {}", dictMap != null ? dictMap.size() : 0);
        } catch (Exception e) {
            log.error("Failed to cache dict data", e);
        }
    }

    public Map<String, List<DictItemDTO>> getAll() {
        if (redisTemplate == null || objectMapper == null) {
            return null;
        }
        try {
            String json = redisTemplate.opsForValue().get(DICT_ALL_CACHE_KEY);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, List<DictItemDTO>>>() {});
        } catch (Exception e) {
            log.error("Failed to read dict cache", e);
            return null;
        }
    }

    public List<DictItemDTO> getByTypeFromCache(String type) {
        Map<String, List<DictItemDTO>> all = getAll();
        if (all == null) {
            return null;
        }
        return all.getOrDefault(type, Collections.emptyList());
    }

    public void clear() {
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.delete(DICT_ALL_CACHE_KEY);
            log.info("Dict cache cleared.");
        } catch (Exception e) {
            log.error("Failed to clear dict cache", e);
        }
    }
}
