package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS 供应商配置
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss_config")
public class SysOssConfig extends BaseEntity {

	private String configName;

	private String provider;

	private String endpoint;

	private String bucket;

	private String accessKey;

	private String secretKey;

	private String cdnDomain;

	private String basePath;

	private Integer isDefault;

	private String remark;

}
