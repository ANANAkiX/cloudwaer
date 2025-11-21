package com.cloudwaer.codegen.serve.controller;

import com.cloudwaer.codegen.api.dto.CodeGenConfigDTO;
import com.cloudwaer.codegen.api.dto.FormFieldConfigDTO;
import com.cloudwaer.codegen.api.dto.TableMetadataDTO;
import com.cloudwaer.codegen.serve.service.FormGeneratorService;
import com.cloudwaer.codegen.serve.service.MetadataService;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 表单生成控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/codegen/form")
@Tag(name = "表单生成管理", description = "表单动态生成和布局调整接口")
public class FormGeneratorController {

    @Autowired
    private FormGeneratorService formGeneratorService;

    @Autowired
    private MetadataService metadataService;

    /**
     * 根据表结构生成表单配置
     */
    @PostMapping("/generate")
    @Operation(summary = "生成表单配置", description = "根据数据库表结构自动生成表单配置")
    public Result<CodeGenConfigDTO> generateFormConfig(@RequestParam Long connectionId,
                                                        @RequestParam String tableName) {
        try {
            CodeGenConfigDTO configDTO = formGeneratorService.generateFormConfig(connectionId, tableName);
            return Result.success(configDTO);
        } catch (Exception e) {
            log.error("生成表单配置失败: connectionId={}, tableName={}", connectionId, tableName, e);
            return Result.fail("生成表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据表结构元数据生成表单字段配置
     */
    @PostMapping("/generate-fields")
    @Operation(summary = "生成表单字段配置", description = "根据表结构元数据生成表单字段配置列表")
    public Result<List<FormFieldConfigDTO>> generateFormFields(@RequestBody @Validated TableMetadataDTO tableMetadata) {
        try {
            List<FormFieldConfigDTO> formFields = formGeneratorService.generateFormFields(tableMetadata);
            return Result.success(formFields);
        } catch (Exception e) {
            log.error("生成表单字段配置失败", e);
            return Result.fail("生成表单字段配置失败: " + e.getMessage());
        }
    }

    /**
     * 保存或更新表单配置
     */
    @PostMapping("/save")
    @Operation(summary = "保存表单配置", description = "保存或更新表单配置")
    public Result<Boolean> saveFormConfig(@RequestBody @Validated CodeGenConfigDTO configDTO) {
        try {
            Boolean result = formGeneratorService.saveOrUpdateFormConfig(configDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("保存表单配置失败", e);
            return Result.fail("保存表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取表单配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取表单配置", description = "根据表名和连接ID获取表单配置，如果不存在则自动生成")
    public Result<CodeGenConfigDTO> getFormConfig(@RequestParam Long connectionId,
                                                   @RequestParam String tableName) {
        try {
            CodeGenConfigDTO configDTO = formGeneratorService.getFormConfig(connectionId, tableName);
            return Result.success(configDTO);
        } catch (Exception e) {
            log.error("获取表单配置失败: connectionId={}, tableName={}", connectionId, tableName, e);
            return Result.fail("获取表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取表单配置
     */
    @GetMapping("/config/{id}")
    @Operation(summary = "根据ID获取表单配置", description = "通过配置ID获取表单配置详情")
    public Result<CodeGenConfigDTO> getFormConfigById(@PathVariable Long id) {
        try {
            CodeGenConfigDTO configDTO = formGeneratorService.getFormConfigById(id);
            return Result.success(configDTO);
        } catch (Exception e) {
            log.error("获取表单配置失败: id={}", id, e);
            return Result.fail("获取表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新表单字段配置（用于布局调整）
     */
    @PutMapping("/fields")
    @Operation(summary = "更新表单字段配置", description = "更新表单字段配置，支持布局调整、字段顺序调整等")
    public Result<Boolean> updateFormFields(@RequestParam Long connectionId,
                                             @RequestParam String tableName,
                                             @RequestBody List<FormFieldConfigDTO> formFields) {
        try {
            // 获取现有配置
            CodeGenConfigDTO configDTO = formGeneratorService.getFormConfig(connectionId, tableName);
            configDTO.setFormFields(formFields);
            
            // 保存更新后的配置
            Boolean result = formGeneratorService.saveOrUpdateFormConfig(configDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新表单字段配置失败: connectionId={}, tableName={}", connectionId, tableName, e);
            return Result.fail("更新表单字段配置失败: " + e.getMessage());
        }
    }

    /**
     * 调整表单布局（批量更新字段的布局信息）
     */
    @PutMapping("/layout")
    @Operation(summary = "调整表单布局", description = "批量更新表单字段的布局信息（行索引、栅格跨度等）")
    public Result<Boolean> adjustFormLayout(@RequestParam Long connectionId,
                                             @RequestParam String tableName,
                                             @RequestBody List<FormFieldConfigDTO> formFields) {
        try {
            // 获取现有配置
            CodeGenConfigDTO configDTO = formGeneratorService.getFormConfig(connectionId, tableName);
            
            // 更新字段布局信息
            for (FormFieldConfigDTO updatedField : formFields) {
                configDTO.getFormFields().stream()
                        .filter(field -> field.getFieldName().equals(updatedField.getFieldName()))
                        .findFirst()
                        .ifPresent(field -> {
                            field.setRowIndex(updatedField.getRowIndex());
                            field.setGridSpan(updatedField.getGridSpan());
                            field.setFormOrder(updatedField.getFormOrder());
                            field.setListOrder(updatedField.getListOrder());
                        });
            }

            // 保存更新后的配置
            Boolean result = formGeneratorService.saveOrUpdateFormConfig(configDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("调整表单布局失败: connectionId={}, tableName={}", connectionId, tableName, e);
            return Result.fail("调整表单布局失败: " + e.getMessage());
        }
    }
}

