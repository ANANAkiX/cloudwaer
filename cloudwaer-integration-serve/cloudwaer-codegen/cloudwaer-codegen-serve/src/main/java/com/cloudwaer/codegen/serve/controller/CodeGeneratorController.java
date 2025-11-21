package com.cloudwaer.codegen.serve.controller;

import com.cloudwaer.codegen.api.dto.CodeGenConfigDTO;
import com.cloudwaer.codegen.serve.service.CodeGeneratorService;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/codegen/generator")
@Tag(name = "代码生成管理", description = "代码生成接口")
public class CodeGeneratorController {

    @Autowired
    private CodeGeneratorService codeGeneratorService;

    /**
     * 生成后端代码
     */
    @PostMapping("/backend")
    @Operation(summary = "生成后端代码", description = "根据配置生成后端代码（Entity、Mapper、Service、Controller、DTO）")
    public Result<Map<String, String>> generateBackendCode(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            if (!Boolean.TRUE.equals(configDTO.getGenerateBackend())) {
                return Result.fail("未启用后端代码生成");
            }
            Map<String, String> generatedFiles = codeGeneratorService.generateBackendCode(configDTO);
            return Result.success(generatedFiles);
        } catch (Exception e) {
            log.error("生成后端代码失败", e);
            return Result.fail("生成后端代码失败: " + e.getMessage());
        }
    }

    /**
     * 生成前端代码
     */
    @PostMapping("/frontend")
    @Operation(summary = "生成前端代码", description = "根据配置生成前端代码（API接口、Vue页面）")
    public Result<Map<String, String>> generateFrontendCode(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            if (!Boolean.TRUE.equals(configDTO.getGenerateFrontend())) {
                return Result.fail("未启用前端代码生成");
            }
            Map<String, String> generatedFiles = codeGeneratorService.generateFrontendCode(configDTO);
            return Result.success(generatedFiles);
        } catch (Exception e) {
            log.error("生成前端代码失败", e);
            return Result.fail("生成前端代码失败: " + e.getMessage());
        }
    }

    /**
     * 生成权限SQL
     */
    @PostMapping("/permission")
    @Operation(summary = "生成权限SQL", description = "根据配置生成权限SQL（默认CRUD权限）")
    public Result<String> generatePermissionSql(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            if (!Boolean.TRUE.equals(configDTO.getGeneratePermission())) {
                return Result.fail("未启用权限SQL生成");
            }
            String sql = codeGeneratorService.generatePermissionSql(configDTO);
            return Result.success(sql);
        } catch (Exception e) {
            log.error("生成权限SQL失败", e);
            return Result.fail("生成权限SQL失败: " + e.getMessage());
        }
    }

    /**
     * 预览生成的文件路径列表
     */
    @PostMapping("/preview")
    @Operation(summary = "预览生成的文件路径", description = "预览将要生成的代码文件路径列表，不生成实际文件")
    public Result<Map<String, Object>> previewGeneratedFiles(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            Map<String, Object> result = codeGeneratorService.previewGeneratedFiles(configDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("预览生成文件列表失败", e);
            return Result.fail("预览生成文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 生成所有代码（后端、前端、权限SQL）并打包为ZIP压缩包
     */
    @PostMapping("/all")
    @Operation(summary = "生成所有代码", description = "根据配置生成后端代码、前端代码和权限SQL，并打包为ZIP压缩包")
    public ResponseEntity<InputStreamResource> generateAllCode(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            // 生成ZIP压缩包
            InputStream zipInputStream = codeGeneratorService.generateAllCodeAsZip(configDTO);
            
            // 构建文件名
            String fileName = String.format("%s_%s_%s.zip", 
                    configDTO.getModuleName(), 
                    configDTO.getEntityName(),
                    System.currentTimeMillis());
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
            
            // 返回文件流
            InputStreamResource resource = new InputStreamResource(zipInputStream);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("生成代码失败", e);
            throw new RuntimeException("生成代码失败: " + e.getMessage(), e);
        }
    }
}

