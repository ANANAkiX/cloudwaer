package com.cloudwaer.admin.serve.service;

import com.cloudwaer.admin.api.dto.DictDTO;
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
 * 字典缓存服务
 */
@Slf4j
@Service
public class DictCacheService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    /**
     * Redis Key：全部字典(type->list)
     */
    private static final String DICT_ALL_CACHE_KEY = "cloudwaer:dict:all";

    /**
     * 将所有字典缓存至Redis（覆盖重建）
     */
    public void cacheAll(Map<String, List<DictDTO>> dictMap) {
        if (redisTemplate == null || objectMapper == null) {
            log.warn("未启用Redis或ObjectMapper，跳过字典缓存");
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(dictMap);
            redisTemplate.opsForValue().set(DICT_ALL_CACHE_KEY, json, 30, TimeUnit.DAYS);
            log.info("字典缓存成功，类型数:{}", dictMap != null ? dictMap.size() : 0);
        } catch (Exception e) {
            log.error("缓存字典失败", e);
        }
    }

    /**
     * 读取全部字典(type->list)
     */
    public Map<String, List<DictDTO>> getAll() {
        if (redisTemplate == null || objectMapper == null) {
            return null;
        }
        try {
            String json = redisTemplate.opsForValue().get(DICT_ALL_CACHE_KEY);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, List<DictDTO>>>(){});
        } catch (Exception e) {
            log.error("读取字典缓存失败", e);
            return null;
        }
    }

    /**
     * 按类型读取列表（从总缓存中拆分）
     */
    public List<DictDTO> getByTypeFromCache(String type) {
        Map<String, List<DictDTO>> all = getAll();
        if (all == null) {
            return null;
        }
        return all.getOrDefault(type, Collections.emptyList());
    }

    /** 清缓存 */
    public void clear() {
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.delete(DICT_ALL_CACHE_KEY);
            log.info("字典缓存已清除");
        } catch (Exception e) {
            log.error("清除字典缓存失败", e);
        }
    }
}
