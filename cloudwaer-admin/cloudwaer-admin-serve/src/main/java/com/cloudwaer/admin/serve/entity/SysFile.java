package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台通用文件元数据
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

	private String fileName;

	private String accessUrl;

	/** LOCAL | ALIYUN_OSS | TENCENT_COS */
	private String provider;

	private Long ossConfigId;

	private String objectKey;

	private String bucket;

	private String contentType;

	private Long fileSize;

	private String md5;

	private String bizType;

	private String bizId;

}
