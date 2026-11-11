package com.cloudwaer.workflow.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WfDefinitionMapper extends BaseMapper<WfDefinition> {

	@Select("SELECT MAX(version) FROM wf_definition WHERE def_key = #{defKey} AND status = 1")
	Integer selectMaxVersion(@Param("defKey") String defKey);

	@Select("SELECT COUNT(*) FROM wf_definition WHERE def_key = #{defKey} AND version = #{version} AND status = 1")
	int countVersion(@Param("defKey") String defKey, @Param("version") Integer version);

	/**
	 * 主列表聚合查询：每个 defKey 只取一个代表版本 （已发布 > 最新草稿 > 最新已下线；归档不参与）。 注意：裸 SQL
	 * 需自行过滤逻辑删除（status=1），否则绕过 @TableLogic 注入。
	 */
	@Select("""
			SELECT * FROM wf_definition d
			WHERE d.status = 1
			  AND d.def_status <> 2
			  AND d.id IN (
			    SELECT t.id FROM (
			      SELECT id, ROW_NUMBER() OVER (
			        PARTITION BY def_key
			        ORDER BY CASE def_status WHEN 1 THEN 3 WHEN 0 THEN 2 ELSE 1 END DESC, version DESC
			      ) AS rn
			      FROM wf_definition
			      WHERE status = 1 AND def_status <> 2
			    ) t
			    WHERE t.rn = 1
			  )
			  AND (#{category} IS NULL OR d.category = #{category})
			  AND (#{keyword} IS NULL OR d.def_key LIKE CONCAT('%', #{keyword}, '%')
			       OR d.def_name LIKE CONCAT('%', #{keyword}, '%'))
			ORDER BY d.update_time DESC
			""")
	IPage<WfDefinition> selectRepresentatives(Page<WfDefinition> page, @Param("category") String category,
			@Param("keyword") String keyword);

}
