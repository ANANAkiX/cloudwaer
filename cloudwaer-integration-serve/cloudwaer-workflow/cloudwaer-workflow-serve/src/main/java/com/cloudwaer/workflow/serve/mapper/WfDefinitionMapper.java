package com.cloudwaer.workflow.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WfDefinitionMapper extends BaseMapper<WfDefinition> {

	@Select("SELECT MAX(version) FROM wf_definition WHERE def_key = #{defKey} AND status = 1")
	Integer selectMaxVersion(@Param("defKey") String defKey);

	@Select("SELECT COUNT(*) FROM wf_definition WHERE def_key = #{defKey} AND version = #{version}")
	int countVersion(@Param("defKey") String defKey, @Param("version") Integer version);

}
