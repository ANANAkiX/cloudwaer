package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.admin.serve.service.DictService;
import com.cloudwaer.common.core.annotation.PermitAll;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dict")
@Tag(name = "通用字典", description = "通用字典接口：按类型查询与立即刷新")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 按类型查询字典
     */
    @GetMapping("/list")
    @Operation(summary = "按类型查询字典", description = "按类型返回字典项列表（优先从Redis读取，缺失时自动重建缓存）")
    @PermitAll
    public Result<List<DictDTO>> listByType(@RequestParam String type) {
        List<DictDTO> list = dictService.getByType(type);
        return Result.success(list);
    }

    /**
     * 立即刷新字典缓存
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新字典缓存", description = "清空Redis后从数据库重建字典缓存")
    public Result<Boolean> refresh() {
        dictService.rebuildCache();
        return Result.success(true);
    }

    // ====== 管理端 CRUD ======
    @GetMapping("/page")
    @Operation(summary = "分页查询字典", description = "按类型与关键字分页查询字典")
    public Result<PageResult<DictDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
                                            @RequestParam(value = "size", defaultValue = "10") Long size,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "type", required = false) String type) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setCurrent(current);
        pageDTO.setSize(size);
        pageDTO.setKeyword(keyword);
        PageResult<DictDTO> pageResult = dictService.page(pageDTO, type);
        return Result.success(pageResult);
    }

    @GetMapping("/detail")
    @Operation(summary = "字典详情", description = "根据ID获取字典详情")
    public Result<DictDTO> detail(@RequestParam("id") Long id) {
        return Result.success(dictService.detail(id));
    }

    @PostMapping("/save")
    @Operation(summary = "新增字典", description = "新增字典项并重建缓存")
    public Result<Boolean> save(@RequestBody DictDTO dto) {
        return Result.success(dictService.save(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新字典", description = "更新字典项并重建缓存")
    public Result<Boolean> update(@RequestBody DictDTO dto) {
        return Result.success(dictService.update(dto));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典", description = "逻辑删除字典项并重建缓存")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(dictService.delete(id));
    }
}
