package com.cloudwaer.blog.serve.service;

import com.cloudwaer.admin.api.dto.SendMessageDTO;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.blog.serve.entity.BlogAuthorApply;
import com.cloudwaer.blog.serve.entity.BlogComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * blog 站内信通知（审核结果类系统提示）。
 *
 * <p>
 * 约定：<b>bizType = 跳转目标类型</b>（blog_article-文章详情 / blog_profile-个人主页），bizId = 目标 id；
 * 标题/内容描述事件本身。通知失败只记日志，不影响审核主流程。
 * </p>
 *
 * <p>
 * 后续规划（点赞/被回复等）在此追加方法即可，机制相同。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class BlogMessageNotifier {

	/**
	 * 跳转目标：文章详情（blog-ui /article/{id}）
	 */
	public static final String LINK_ARTICLE = "blog_article";

	/**
	 * 跳转目标：个人主页（blog-ui /profile）
	 */
	public static final String LINK_PROFILE = "blog_profile";

	@Autowired
	private AdminFeignClient adminFeignClient;

	/**
	 * 文章审核结果 → 通知作者；通过时附文章详情链接
	 */
	public void notifyArticleAuditResult(Long authorId, Long articleId, String title, int auditStatus, String remark) {
		if (authorId == null) {
			return;
		}
		boolean approved = auditStatus == 1;
		String content = approved ? "你的文章《" + title + "》已通过审核并发布。"
				: "你的文章《" + title + "》未通过审核。" + (StringUtils.hasText(remark) ? "审核意见：" + remark : "");
		send(authorId, approved ? "文章审核通过" : "文章审核未通过", content, approved ? LINK_ARTICLE : null,
				approved ? String.valueOf(articleId) : null);
	}

	/**
	 * 评论审核结果 → 通知评论人；附该评论所在文章的链接
	 */
	public void notifyCommentAuditResult(Long commentUserId, Long articleId, int auditStatus) {
		if (commentUserId == null) {
			return;
		}
		boolean approved = auditStatus == 1;
		String content = approved ? "你的评论已通过审核并展示。" : "你的评论未通过审核。";
		send(commentUserId, approved ? "评论审核通过" : "评论审核未通过", content, LINK_ARTICLE, String.valueOf(articleId));
	}

	/**
	 * 博主申请结果 → 通知申请人；通过时附个人主页链接
	 */
	public void notifyAuthorApplyResult(BlogAuthorApply apply, int applyStatus) {
		if (apply == null || apply.getUserId() == null) {
			return;
		}
		boolean approved = applyStatus == 1;
		String content = approved ? "你的博客主申请已通过，快去创作吧。"
				: "你的博客主申请未通过。" + (StringUtils.hasText(apply.getHandleRemark()) ? "意见：" + apply.getHandleRemark() : "");
		send(apply.getUserId(), approved ? "博客主申请通过" : "博客主申请未通过", content, approved ? LINK_PROFILE : null, null);
	}

	/**
	 * 文章被评论 → 通知作者（评论/回复均触发，评论审核通过时调用）
	 */
	public void notifyArticleCommented(Long articleAuthorId, Long commenterId, String commenterName, Long articleId,
			String articleTitle, String snippet) {
		if (articleAuthorId == null || articleAuthorId.equals(commenterId)) {
			return;
		}
		String content = (StringUtils.hasText(commenterName) ? commenterName : "有人") + " 评论了你的文章《" + articleTitle + "》："
				+ (snippet == null ? "" : snippet);
		send(articleAuthorId, "文章收到新评论", content, LINK_ARTICLE, String.valueOf(articleId));
	}

	/**
	 * 评论被回复 → 通知被回复人（评论审核通过时调用）
	 */
	public void notifyCommentReplied(Long targetUserId, Long replierId, String replierName, Long articleId,
			String snippet) {
		if (targetUserId == null || targetUserId.equals(replierId)) {
			return;
		}
		send(targetUserId, "你的评论有新回复",
				(StringUtils.hasText(replierName) ? replierName : "有人") + " 回复了你：" + (snippet == null ? "" : snippet),
				LINK_ARTICLE, String.valueOf(articleId));
	}

	/**
	 * 文章被点赞 → 通知作者
	 */
	public void notifyArticleLiked(Long authorId, Long likerId, String likerName, Long articleId, String title) {
		if (authorId == null || authorId.equals(likerId)) {
			return;
		}
		send(authorId, "文章收到点赞", (StringUtils.hasText(likerName) ? likerName : "有人") + " 赞了你的文章《" + title + "》",
				LINK_ARTICLE, String.valueOf(articleId));
	}

	/**
	 * 评论被点赞 → 通知评论人
	 */
	public void notifyCommentLiked(Long commentUserId, Long likerId, String likerName, Long articleId,
			String commentSnippet) {
		if (commentUserId == null || commentUserId.equals(likerId)) {
			return;
		}
		String snippet = commentSnippet == null ? "" : commentSnippet;
		send(commentUserId, "评论收到点赞", (StringUtils.hasText(likerName) ? likerName : "有人") + " 赞了你的评论：" + snippet,
				LINK_ARTICLE, String.valueOf(articleId));
	}

	private void send(Long userId, String title, String content, String bizType, String bizId) {
		try {
			SendMessageDTO dto = new SendMessageDTO();
			dto.setUserIds(List.of(userId));
			dto.setMsgType("SYSTEM");
			dto.setTitle(title);
			dto.setContent(content);
			dto.setBizType(bizType);
			dto.setBizId(bizId);
			adminFeignClient.sendMessage(dto);
		}
		catch (Exception e) {
			log.warn("站内信发送失败（不影响主流程）: userId={}, title={}, err={}", userId, title, e.getMessage());
		}
	}

}
