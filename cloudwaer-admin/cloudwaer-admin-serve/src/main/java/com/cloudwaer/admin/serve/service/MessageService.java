package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.admin.api.dto.SendMessageDTO;
import com.cloudwaer.admin.serve.dto.MessageItemVO;
import com.cloudwaer.admin.serve.entity.SysMessage;
import com.cloudwaer.admin.serve.entity.SysMessageRecipient;
import com.cloudwaer.admin.serve.mapper.SysMessageMapper;
import com.cloudwaer.admin.serve.mapper.SysMessageRecipientMapper;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.ws.WsDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 站内信服务（平台能力：业务模块经 AdminFeignClient.sendMessage 调用）。
 *
 * <p>
 * <b>已规划的系统提示场景（后续在 blog/admin 侧接入，接入时只需调用 {@link #sendToUsers}）</b>：
 * </p>
 * <ul>
 * <li>文章审核通过 / 未通过 → 通知作者（bizType=blog_article）</li>
 * <li>评论审核通过 → 通知评论人（bizType=blog_comment）</li>
 * <li>评论被回复 → 通知被回复人（bizType=blog_comment_reply）</li>
 * <li>文章被点赞 → 通知文章作者（bizType=blog_article_like）</li>
 * </ul>
 *
 * <p>
 * <b>私信（用户↔用户）入口预留</b>：msg_type=PRIVATE 已预留；产品形态（防骚扰/黑名单/会话流）未定， 暂不开放用户侧发送 API。开放时在本服务加
 * {@code sendPrivate} 并提供 /message/private 接口即可， 发送机制与 {@link #sendToUsers} 相同。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class MessageService {

	public static final String MSG_TYPE_NOTICE = "NOTICE";

	public static final String MSG_TYPE_SYSTEM = "SYSTEM";

	/**
	 * 私信（预留，暂不开放用户侧发送）
	 */
	public static final String MSG_TYPE_PRIVATE = "PRIVATE";

	@Autowired
	private SysMessageMapper messageMapper;

	@Autowired
	private SysMessageRecipientMapper recipientMapper;

	@Autowired
	private WsDispatcher wsDispatcher;

	/**
	 * 发送给单个用户
	 */
	public Boolean send(Long userId, String msgType, String title, String content, String bizType, String bizId) {
		return sendToUsers(userId == null ? List.of() : List.of(userId), msgType, title, content, bizType, bizId, null,
				null);
	}

	/**
	 * 发送给多个用户（Feign 内部调用入口）
	 */
	public Boolean send(SendMessageDTO dto) {
		return sendToUsers(dto.getUserIds(), dto.getMsgType(), dto.getTitle(), dto.getContent(), dto.getBizType(),
				dto.getBizId(), dto.getSenderId(), dto.getSenderName());
	}

	public Boolean sendToUsers(List<Long> userIds, String msgType, String title, String content, String bizType,
			String bizId, Long senderId, String senderName) {
		if (userIds == null || userIds.isEmpty()) {
			return Boolean.FALSE;
		}
		if (!StringUtils.hasText(title)) {
			throw new BusinessException("消息标题不能为空");
		}
		SysMessage message = new SysMessage();
		message.setMsgType(StringUtils.hasText(msgType) ? msgType : MSG_TYPE_SYSTEM);
		message.setTitle(title);
		message.setContent(content);
		message.setBizType(bizType);
		message.setBizId(bizId);
		message.setSenderId(senderId);
		// 系统消息展示名兜底「系统」
		message.setSenderName(StringUtils.hasText(senderName) ? senderName : "系统");
		messageMapper.insert(message);

		List<Long> distinctUserIds = userIds.stream().filter(java.util.Objects::nonNull).distinct().toList();
		for (Long userId : distinctUserIds) {
			SysMessageRecipient recipient = new SysMessageRecipient();
			recipient.setMessageId(message.getId());
			recipient.setUserId(userId);
			recipient.setReadFlag(0);
			recipientMapper.insert(recipient);
			pushMessage(userId, message, recipient.getId());
		}
		log.info("站内信已发送: messageId={}, type={}, receivers={}", message.getId(), message.getMsgType(),
				distinctUserIds.size());
		return Boolean.TRUE;
	}

	/**
	 * 未读数
	 */
	public Long unreadCount(Long userId) {
		return recipientMapper
			.selectCount(new LambdaQueryWrapper<SysMessageRecipient>().eq(SysMessageRecipient::getUserId, userId)
				.eq(SysMessageRecipient::getReadFlag, 0));
	}

	/**
	 * 收件箱（分页，按最新在前）
	 */
	public Page<MessageItemVO> inbox(Long userId, long page, long size) {
		Page<SysMessageRecipient> recipientPage = recipientMapper.selectPage(new Page<>(page, size),
				new LambdaQueryWrapper<SysMessageRecipient>().eq(SysMessageRecipient::getUserId, userId)
					.orderByDesc(SysMessageRecipient::getId));
		List<SysMessageRecipient> recipients = recipientPage.getRecords();
		if (recipients.isEmpty()) {
			Page<MessageItemVO> empty = new Page<>(page, size, recipientPage.getTotal());
			empty.setRecords(List.of());
			return empty;
		}
		Map<Long, SysMessage> messageMap = messageMapper
			.selectBatchIds(recipients.stream().map(SysMessageRecipient::getMessageId).distinct().toList())
			.stream()
			.collect(Collectors.toMap(SysMessage::getId, Function.identity()));
		List<MessageItemVO> rows = new ArrayList<>();
		for (SysMessageRecipient recipient : recipients) {
			SysMessage message = messageMap.get(recipient.getMessageId());
			if (message == null) {
				continue;
			}
			MessageItemVO vo = new MessageItemVO();
			vo.setRecipientId(recipient.getId());
			vo.setMessageId(message.getId());
			vo.setMsgType(message.getMsgType());
			vo.setTitle(message.getTitle());
			vo.setContent(message.getContent());
			vo.setBizType(message.getBizType());
			vo.setBizId(message.getBizId());
			vo.setSenderName(message.getSenderName());
			vo.setReadFlag(recipient.getReadFlag());
			vo.setCreateTime(message.getCreateTime());
			rows.add(vo);
		}
		Page<MessageItemVO> result = new Page<>(page, size, recipientPage.getTotal());
		result.setRecords(rows);
		return result;
	}

	/**
	 * 批量标记已读
	 */
	public Boolean markRead(Long userId, List<Long> recipientIds) {
		if (recipientIds == null || recipientIds.isEmpty()) {
			return Boolean.FALSE;
		}
		recipientMapper.update(null,
				new LambdaUpdateWrapper<SysMessageRecipient>().eq(SysMessageRecipient::getUserId, userId)
					.in(SysMessageRecipient::getId, recipientIds)
					.set(SysMessageRecipient::getReadFlag, 1)
					.set(SysMessageRecipient::getReadTime, LocalDateTime.now()));
		pushUnread(userId);
		return Boolean.TRUE;
	}

	/**
	 * 全部已读
	 */
	public Boolean markAllRead(Long userId) {
		recipientMapper.update(null,
				new LambdaUpdateWrapper<SysMessageRecipient>().eq(SysMessageRecipient::getUserId, userId)
					.eq(SysMessageRecipient::getReadFlag, 0)
					.set(SysMessageRecipient::getReadFlag, 1)
					.set(SysMessageRecipient::getReadTime, LocalDateTime.now()));
		pushUnread(userId);
		return Boolean.TRUE;
	}

	/**
	 * 删除收件箱条目（逻辑删）
	 */
	public Boolean remove(Long userId, Long recipientId) {
		SysMessageRecipient recipient = recipientMapper.selectById(recipientId);
		if (recipient == null || !userId.equals(recipient.getUserId())) {
			throw new BusinessException("消息不存在");
		}
		recipientMapper.deleteById(recipientId);
		pushUnread(userId);
		return Boolean.TRUE;
	}

	private void pushMessage(Long userId, SysMessage message, Long recipientId) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("recipientId", String.valueOf(recipientId));
		payload.put("messageId", String.valueOf(message.getId()));
		payload.put("msgType", message.getMsgType());
		payload.put("title", message.getTitle());
		payload.put("senderName", message.getSenderName());
		payload.put("bizType", message.getBizType());
		payload.put("bizId", message.getBizId());
		payload.put("createTime", String.valueOf(message.getCreateTime()));
		payload.put("unreadCount", unreadCount(userId));
		wsDispatcher.pushUser(userId, "message", payload);
	}

	/**
	 * 推送未读数（连接建立时 / 已读变更时）
	 */
	public void pushUnread(Long userId) {
		wsDispatcher.pushUser(userId, "unread", Map.of("count", unreadCount(userId)));
	}

}
