package com.cloudwaer.common.core.util;

import com.cloudwaer.common.core.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

/**
 * 安全上下文工具类
 * 提供获取当前用户、Token等常用方法
 *
 * @author cloudwaer
 */
public class SecurityContextUtil {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 获取当前请求的HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 从请求头中获取Token
     *
     * @param request HttpServletRequest
     * @return Token
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取当前请求的Token
     *
     * @return Token
     */
    public static String getCurrentToken() {
        HttpServletRequest request = getRequest();
        return getTokenFromRequest(request);
    }

    /**
     * 从Token UUID中获取Claims（从Redis获取JWT Token后解析）
     *
     * @return Claims
     */
    public static Claims getCurrentClaims() {
        String tokenUuid = getCurrentToken();
        if (tokenUuid == null) {
            return null;
        }
        try {
            TokenService tokenService = SpringContextUtil.getBean(TokenService.class);
            if (tokenService == null) {
                return null;
            }
            
            // 从Redis获取JWT Token
            String jwtToken = tokenService.getJwtToken(tokenUuid);
            if (jwtToken == null) {
                return null;
            }
            
            // 解析JWT Token获取Claims
            JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
            return jwtUtil.getClaimsFromToken(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token UUID获取Token数据（包含用户信息、角色、权限）
     *
     * @return Token数据Map
     */
    public static Map<String, Object> getCurrentTokenData() {
        String tokenUuid = getCurrentToken();
        if (tokenUuid == null) {
            return null;
        }
        try {
            TokenService tokenService = SpringContextUtil.getBean(TokenService.class);
            if (tokenService == null) {
                return null;
            }
            return tokenService.getTokenData(tokenUuid);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户权限代码列表
     *
     * @return 权限代码列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getCurrentPermissions() {
        Map<String, Object> tokenData = getCurrentTokenData();
        if (tokenData != null) {
            Object permissions = tokenData.get("permissions");
            if (permissions instanceof List) {
                return (List<String>) permissions;
            }
        }
        // 如果从Redis获取失败，尝试从JWT Claims获取
        Claims claims = getCurrentClaims();
        if (claims != null) {
            Object permissions = claims.get("permissions");
            if (permissions instanceof List) {
                return (List<String>) permissions;
            }
        }
        return null;
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        // 从Token中获取
        Claims claims = getCurrentClaims();
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId != null) {
                if (userId instanceof Long) {
                    return (Long) userId;
                } else if (userId instanceof Integer) {
                    return ((Integer) userId).longValue();
                } else if (userId instanceof String) {
                    return Long.parseLong((String) userId);
                }
            }
        }
        return null;
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        // 从Token中获取
        Claims claims = getCurrentClaims();
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    /**
     * 获取当前用户角色ID列表
     *
     * @return 角色ID列表
     */
    @SuppressWarnings("unchecked")
    public static List<Long> getCurrentRoleIds() {
        Claims claims = getCurrentClaims();
        if (claims != null) {
            Object roleIds = claims.get("roleIds");
            if (roleIds instanceof List) {
                return (List<Long>) roleIds;
            }
        }
        return null;
    }

    /**
     * 获取当前用户角色ID（第一个）
     *
     * @return 角色ID
     */
    public static Long getCurrentRoleId() {
        Claims claims = getCurrentClaims();
        if (claims != null) {
            Object roleId = claims.get("roleId");
            if (roleId != null) {
                if (roleId instanceof Long) {
                    return (Long) roleId;
                } else if (roleId instanceof Integer) {
                    return ((Integer) roleId).longValue();
                } else if (roleId instanceof String) {
                    return Long.parseLong((String) roleId);
                }
            }
        }
        return null;
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isAuthenticated() {
        String token = getCurrentToken();
        if (token == null) {
            return false;
        }
        try {
            JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
}

