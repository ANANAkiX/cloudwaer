package com.cloudwaer.flowable.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.flowable.serve.entity.WfModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WfModelMapper extends BaseMapper<WfModel> {

	/**
	 * 查询最大版本号（含逻辑删除行），避免 uk_model_key_version 冲突。
	 */
	@Select("SELECT COALESCE(MAX(version), 0) FROM wf_model WHERE model_key = #{modelKey}")
	Integer selectMaxVersionIncludingDeleted(@Param("modelKey") String modelKey);

	/**
	 * 判断版本是否存在（含逻辑删除行）。
	 */
	@Select("SELECT COUNT(1) FROM wf_model WHERE model_key = #{modelKey} AND version = #{version}")
	int countVersionIncludingDeleted(@Param("modelKey") String modelKey, @Param("version") int version);

}
