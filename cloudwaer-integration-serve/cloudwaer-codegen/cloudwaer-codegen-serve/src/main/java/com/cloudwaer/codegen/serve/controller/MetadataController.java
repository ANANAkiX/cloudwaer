package com.cloudwaer.codegen.serve.controller;

import com.cloudwaer.codegen.api.dto.TableMetadataDTO;
import com.cloudwaer.codegen.serve.service.MetadataService;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库元数据管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/codegen/metadata")
@Tag(name = "数据库元数据管理", description = "数据库表结构元数据查询接口")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    /**
     * 获取数据库中的所有表列表
     */
    @GetMapping("/tables")
    @Operation(summary = "获取数据库表列表", description = "获取指定数据库连接下的所有表名")
    public Result<List<String>> getTableList(@RequestParam Long connectionId) {
        try {
            List<String> tableList = metadataService.getTableList(connectionId);
            return Result.success(tableList);
        } catch (Exception e) {
            log.error("获取数据库表列表失败", e);
            return Result.fail("获取数据库表列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取表结构元数据
     */
    @GetMapping("/table")
    @Operation(summary = "获取表结构元数据", description = "获取指定表的详细结构信息，包括列信息、主键等")
    public Result<TableMetadataDTO> getTableMetadata(@RequestParam Long connectionId,
                                                      @RequestParam String tableName) {
        try {
            TableMetadataDTO metadata = metadataService.getTableMetadata(connectionId, tableName);
            return Result.success(metadata);
        } catch (Exception e) {
            log.error("获取表结构元数据失败: connectionId={}, tableName={}", connectionId, tableName, e);
            return Result.fail("获取表结构元数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取多个表的结构元数据
     */
    @PostMapping("/tables/metadata")
    @Operation(summary = "批量获取表结构元数据", description = "批量获取多个表的详细结构信息")
    public Result<List<TableMetadataDTO>> getTableMetadataList(@RequestParam Long connectionId,
                                                                @RequestBody List<String> tableNames) {
        try {
            List<TableMetadataDTO> metadataList = metadataService.getTableMetadataList(connectionId, tableNames);
            return Result.success(metadataList);
        } catch (Exception e) {
            log.error("批量获取表结构元数据失败: connectionId={}, tableNames={}", connectionId, tableNames, e);
            return Result.fail("批量获取表结构元数据失败: " + e.getMessage());
        }
    }
}

