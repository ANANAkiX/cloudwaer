package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.SysUserOauth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * 第三方账号绑定 Mapper
 * <p>
 * 注意：本表有唯一索引（provider_type+provider_user_id / user_id+provider_type），
 * 中间表必须物理删除——逻辑删除（status=0）的行仍占唯一索引，会导致换绑/他人绑定撞 Duplicate key。
 *
 * @author cloudwaer
 */
public interface SysUserOauthMapper extends BaseMapper<SysUserOauth> {

	/**
	 * 物理删除用户在某供应商下的绑定（解绑用，不走逻辑删除）
	 */
	@Delete("DELETE FROM sys_user_oauth WHERE user_id = #{userId} AND provider_type = #{providerType}")
	int deleteByUserAndProvider(@Param("userId") Long userId, @Param("providerType") String providerType);

	/**
	 * 物理清理唯一索引位置上的失效遗留行（历史逻辑删除产生，status != 1），插入绑定前调用
	 */
	@Delete("DELETE FROM sys_user_oauth WHERE status <> 1 AND ((provider_type = #{providerType} AND provider_user_id = #{providerUserId}) OR (user_id = #{userId} AND provider_type = #{providerType}))")
	int purgeInactive(@Param("userId") Long userId, @Param("providerType") String providerType,
			@Param("providerUserId") String providerUserId);

}
