package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.entity.User;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author cloudwaer
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户DTO
     */
    UserDTO getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱
     * @return 用户DTO
     */
    UserDTO getUserByEmail(String email);


    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 分页查询用户列表
     *
     * @param pageDTO 分页参数
     * @return 分页结果
     */
    PageResult<UserDTO> getUserPage(PageDTO pageDTO);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long id);

    /**
     * 保存用户
     *
     * @param userDTO 用户DTO
     * @return 是否成功
     */
    Boolean saveUser(UserDTO userDTO);

    /**
     * 更新用户
     *
     * @param userDTO 用户DTO
     * @return 是否成功
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    Boolean deleteUser(Long id);

    /**
     * 分配角色给用户
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    Boolean assignRoles(Long userId, List<Long> roleIds);
}



