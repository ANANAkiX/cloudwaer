package com.cloudwaer.blog.serve.task;

import com.cloudwaer.blog.serve.service.BlogTopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 博客热度兜底重算（交互时已增量刷新；定时全量校准）
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class BlogHeatTask {

	@Autowired
	private BlogTopicService blogTopicService;

	/**
	 * 每小时 15 分刷新已发布文章热度
	 */
	@Scheduled(cron = "0 15 * * * ?")
	public void refreshArticleHeat() {
		try {
			int n = blogTopicService.refreshArticleHeat();
			log.info("文章热度定时刷新完成: {} 条", n);
		}
		catch (Exception e) {
			log.error("文章热度定时刷新失败", e);
		}
	}

}
