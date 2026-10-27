package com.cloudwaer.wiki.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.wiki.api.dto.WikiShareAccessDTO;
import com.cloudwaer.wiki.api.dto.WikiShareDTO;
import com.cloudwaer.wiki.api.dto.WikiShareSaveDTO;
import com.cloudwaer.wiki.serve.service.WikiShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wiki")
@RequiredArgsConstructor
@Tag(name = "知识库分享", description = "管理端生成/吊销；开放访问 /wiki/open/share")
public class WikiShareController {

	private final WikiShareService wikiShareService;

	@GetMapping("/share/page")
	@PermissionCode("wiki:share:view")
	@Operation(summary = "分享分页")
	public Result<PageResult<WikiShareDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "targetType", required = false) String targetType,
			@RequestParam(value = "targetId", required = false) Long targetId) {
		return Result.success(wikiShareService.page(current, size, targetType, targetId));
	}

	@PostMapping("/share/save")
	@PermissionCode("wiki:share:add")
	@Operation(summary = "创建分享链接")
	public Result<WikiShareDTO> create(@RequestBody @Valid WikiShareSaveDTO dto) {
		return Result.success(wikiShareService.create(dto));
	}

	@DeleteMapping("/share/revoke")
	@PermissionCode("wiki:share:revoke")
	@Operation(summary = "吊销分享")
	public Result<Boolean> revoke(@RequestParam("id") Long id) {
		return Result.success(wikiShareService.revoke(id));
	}

	@PostMapping("/open/share/update")
	@Operation(summary = "分享写入", description = "仅 accessLevel=2 的文档分享")
	public Result<WikiShareAccessDTO> openShareUpdate(@RequestParam("token") String token,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "title", required = false) String title, @RequestParam("content") String content) {
		return Result.success(wikiShareService.updateViaShare(token, password, title, content));
	}

	@GetMapping("/open/share")
	@Operation(summary = "匿名访问分享", description = "token 必填；有密码时 password 必填")
	public Result<WikiShareAccessDTO> openShare(@RequestParam("token") String token,
			@RequestParam(value = "password", required = false) String password) {
		return Result.success(wikiShareService.access(token, password));
	}

}
