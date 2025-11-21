package com.cloudwaer.codegen.serve.service;

import com.cloudwaer.codegen.api.dto.CodeGenConfigDTO;

import java.io.InputStream;
import java.util.Map;

/**
 * 代码生成服务接口
 *
 * @author cloudwaer
 */
public interface CodeGeneratorService {

    /**
     * 生成后端代码
     *
     * @param configDTO 代码生成配置
     * @return 生成的文件路径映射（文件相对路径 -> 文件内容）
     */
    Map<String, String> generateBackendCode(CodeGenConfigDTO configDTO);

    /**
     * 生成前端代码
     *
     * @param configDTO 代码生成配置
     * @return 生成的文件路径映射（文件相对路径 -> 文件内容）
     */
    Map<String, String> generateFrontendCode(CodeGenConfigDTO configDTO);

    /**
     * 生成权限SQL
     *
     * @param configDTO 代码生成配置
     * @return 权限SQL语句
     */
    String generatePermissionSql(CodeGenConfigDTO configDTO);

    /**
     * 生成所有代码并打包为ZIP压缩包
     *
     * @param configDTO 代码生成配置
     * @return ZIP压缩包的输入流
     */
    InputStream generateAllCodeAsZip(CodeGenConfigDTO configDTO);

    /**
     * 预览生成的文件路径和内容（不生成实际文件）
     *
     * @param configDTO 代码生成配置
     * @return 文件路径和内容映射（按目录结构组织）
     */
    Map<String, Object> previewGeneratedFiles(CodeGenConfigDTO configDTO);
}

