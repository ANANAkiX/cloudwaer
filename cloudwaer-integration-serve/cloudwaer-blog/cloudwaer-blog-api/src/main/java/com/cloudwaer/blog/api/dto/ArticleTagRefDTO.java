package com.cloudwaer.blog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章标签引用DTO
 *
 * @author cloudwaer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagRefDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标签ID
	 */
	private Long id;

	/**
	 * 标签名
	 */
	private String name;

}
