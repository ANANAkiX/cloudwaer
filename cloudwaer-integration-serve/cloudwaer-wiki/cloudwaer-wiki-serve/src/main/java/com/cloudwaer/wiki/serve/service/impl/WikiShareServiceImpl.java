package com.cloudwaer.wiki.serve.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.wiki.api.dto.WikiDocDTO;
import com.cloudwaer.wiki.api.dto.WikiShareAccessDTO;
import com.cloudwaer.wiki.api.dto.WikiShareDTO;
import com.cloudwaer.wiki.api.dto.WikiShareSaveDTO;
import com.cloudwaer.wiki.serve.entity.WikiDoc;
import com.cloudwaer.wiki.serve.entity.WikiShare;
import com.cloudwaer.wiki.serve.entity.WikiSpace;
import com.cloudwaer.wiki.serve.mapper.WikiDocMapper;
import com.cloudwaer.wiki.serve.mapper.WikiShareMapper;
import com.cloudwaer.wiki.serve.mapper.WikiSpaceMapper;
import com.cloudwaer.wiki.serve.service.WikiShareService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WikiShareServiceImpl implements WikiShareService {

	private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	private final WikiShareMapper shareMapper;

	private final WikiSpaceMapper spaceMapper;

	private final WikiDocMapper docMapper;

	@Override
	public PageResult<WikiShareDTO> page(Long current, Long size, String targetType, Long targetId) {
		LambdaQueryWrapper<WikiShare> qw = new LambdaQueryWrapper<WikiShare>()
			.eq(StringUtils.hasText(targetType), WikiShare::getTargetType, targetType)
			.eq(targetId != null, WikiShare::getTargetId, targetId)
			.orderByDesc(WikiShare::getCreateTime);
		Page<WikiShare> page = shareMapper.selectPage(new Page<>(current, size), qw);
		List<WikiShareDTO> records = page.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		return new PageResult<>(records, page.getTotal(), current, size);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WikiShareDTO create(WikiShareSaveDTO dto) {
		validateTarget(dto.getTargetType(), dto.getTargetId());
		WikiShare entity = new WikiShare();
		entity.setTargetType(dto.getTargetType());
		entity.setTargetId(dto.getTargetId());
		entity.setAccessLevel(dto.getAccessLevel() == null ? 1 : dto.getAccessLevel());
		if (entity.getAccessLevel() != 1 && entity.getAccessLevel() != 2) {
			throw new BusinessException("访问级别无效");
		}
		entity.setExpireTime(parseExpire(dto.getExpireTime()));
		if (StringUtils.hasText(dto.getPassword())) {
			entity.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
		}
		entity.setRemark(dto.getRemark());
		entity.setToken(IdUtil.fastSimpleUUID() + IdUtil.fastSimpleUUID());
		entity.setStatus(1);
		shareMapper.insert(entity);
		return toDto(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean revoke(Long id) {
		WikiShare entity = shareMapper.selectById(id);
		if (entity == null) {
			throw new BusinessException("分享不存在");
		}
		WikiShare upd = new WikiShare();
		upd.setId(id);
		upd.setStatus(0);
		return shareMapper.updateById(upd) > 0;
	}

	@Override
	public WikiShareAccessDTO updateViaShare(String token, String password, String title, String content) {
		WikiShare share = requireValidShare(token, password);
		if (share.getAccessLevel() == null || share.getAccessLevel() != 2) {
			throw new BusinessException("该分享为只读，无法编辑");
		}
		if (!"doc".equals(share.getTargetType())) {
			throw new BusinessException("仅文档分享支持在线编辑");
		}
		WikiDoc doc = docMapper.selectById(share.getTargetId());
		if (doc == null) {
			throw new BusinessException("文档不存在");
		}
		if (title != null && !title.isBlank()) {
			doc.setTitle(title.trim());
		}
		doc.setContent(content == null ? "" : content);
		if (content != null && !content.isBlank()) {
			String plain = content.replaceAll("\\s+", " ").trim();
			doc.setSummary(plain.length() > 200 ? plain.substring(0, 200) : plain);
		}
		docMapper.updateById(doc);
		return access(token, password);
	}

	private WikiShare requireValidShare(String token, String password) {
		WikiShare share = shareMapper.selectOne(new LambdaQueryWrapper<WikiShare>().eq(WikiShare::getToken, token));
		if (share == null || share.getStatus() == null || share.getStatus() != 1) {
			throw new BusinessException("分享不存在或已失效");
		}
		if (isExpired(share.getExpireTime())) {
			throw new BusinessException("分享已过期（有效期至 " + share.getExpireTime() + "）");
		}
		if (StringUtils.hasText(share.getPassword())) {
			String raw = password == null ? "" : password;
			if (!share.getPassword().equals(DigestUtils.md5DigestAsHex(raw.getBytes()))) {
				throw new BusinessException("访问密码错误");
			}
		}
		return share;
	}

	@Override
	public WikiShareAccessDTO access(String token, String password) {
		WikiShare share = shareMapper.selectOne(new LambdaQueryWrapper<WikiShare>().eq(WikiShare::getToken, token));
		if (share == null || share.getStatus() == null || share.getStatus() != 1) {
			throw new BusinessException("分享不存在或已失效");
		}
		if (isExpired(share.getExpireTime())) {
			throw new BusinessException("分享已过期（有效期至 " + share.getExpireTime() + "）");
		}
		if (StringUtils.hasText(share.getPassword())) {
			String raw = password == null ? "" : password;
			if (raw.isEmpty()) {
				// 未提交密码：只探测是否需要密码，不报错
				WikiShareAccessDTO stub = new WikiShareAccessDTO();
				stub.setToken(share.getToken());
				stub.setTargetType(share.getTargetType());
				stub.setTargetId(share.getTargetId());
				stub.setAccessLevel(share.getAccessLevel());
				stub.setNeedPassword(true);
				return stub;
			}
			if (!share.getPassword().equals(DigestUtils.md5DigestAsHex(raw.getBytes()))) {
				throw new BusinessException("访问密码错误");
			}
		}
		WikiShareAccessDTO out = new WikiShareAccessDTO();
		out.setNeedPassword(false);
		out.setToken(share.getToken());
		out.setTargetType(share.getTargetType());
		out.setTargetId(share.getTargetId());
		out.setAccessLevel(share.getAccessLevel());
		if ("doc".equals(share.getTargetType())) {
			WikiDoc doc = docMapper.selectById(share.getTargetId());
			if (doc == null) {
				throw new BusinessException("文档不存在");
			}
			out.setTitle(doc.getTitle());
			out.setContent(doc.getContent());
			WikiDocDTO one = new WikiDocDTO();
			BeanUtils.copyProperties(doc, one);
			one.setChildren(new ArrayList<>());
			out.setDocs(List.of(one));
		}
		else if ("space".equals(share.getTargetType())) {
			WikiSpace space = spaceMapper.selectById(share.getTargetId());
			if (space == null) {
				throw new BusinessException("空间不存在");
			}
			out.setTitle(space.getName());
			List<WikiDoc> docs = docMapper
				.selectList(new LambdaQueryWrapper<WikiDoc>().eq(WikiDoc::getSpaceId, share.getTargetId())
					.orderByAsc(WikiDoc::getSort));
			out.setDocs(docs.stream().map(d -> {
				WikiDocDTO dto = new WikiDocDTO();
				BeanUtils.copyProperties(d, dto);
				dto.setContent(null);
				dto.setChildren(new ArrayList<>());
				return dto;
			}).collect(Collectors.toList()));
		}
		else {
			throw new BusinessException("分享类型无效");
		}
		return out;
	}

	private void validateTarget(String type, Long id) {
		if ("space".equals(type)) {
			if (spaceMapper.selectById(id) == null) {
				throw new BusinessException("空间不存在");
			}
		}
		else if ("doc".equals(type)) {
			if (docMapper.selectById(id) == null) {
				throw new BusinessException("文档不存在");
			}
		}
		else {
			throw new BusinessException("目标类型无效");
		}
	}

	/**
	 * 解析有效期：空串/undefined/null 不设；非法格式报错；已过期时间拒收。
	 */
	private LocalDateTime parseExpire(String raw) {
		if (!StringUtils.hasText(raw)) {
			return null;
		}
		String s = raw.trim();
		if ("null".equalsIgnoreCase(s) || "undefined".equalsIgnoreCase(s) || "Invalid date".equalsIgnoreCase(s)) {
			return null;
		}
		LocalDateTime expire;
		try {
			if (s.length() == 10) {
				expire = LocalDate.parse(s).atTime(23, 59, 59);
			}
			else {
				String norm = s.replace(' ', 'T');
				if (norm.length() > 16) {
					norm = norm.substring(0, 16);
				}
				expire = LocalDateTime.parse(norm, ISO);
			}
		}
		catch (Exception ex) {
			throw new BusinessException("有效期格式应为 yyyy-MM-ddTHH:mm 或 yyyy-MM-dd");
		}
		if (expire.getYear() <= 1971) {
			return null;
		}
		if (!expire.isAfter(LocalDateTime.now())) {
			throw new BusinessException("有效期不能早于当前时间，留空表示长期有效");
		}
		return expire;
	}

	private boolean isExpired(LocalDateTime expire) {
		if (expire == null) {
			return false;
		}
		if (expire.getYear() <= 1971) {
			return false;
		}
		return expire.isBefore(LocalDateTime.now());
	}

	private WikiShareDTO toDto(WikiShare entity) {
		WikiShareDTO dto = new WikiShareDTO();
		BeanUtils.copyProperties(entity, dto);
		dto.setHasPassword(StringUtils.hasText(entity.getPassword()));
		dto.setExpired(entity.getExpireTime() != null && entity.getExpireTime().isBefore(LocalDateTime.now()));
		dto.setRevoked(entity.getStatus() == null || entity.getStatus() != 1);
		dto.setShareUrl("/wiki/s/" + entity.getToken());
		return dto;
	}

}
