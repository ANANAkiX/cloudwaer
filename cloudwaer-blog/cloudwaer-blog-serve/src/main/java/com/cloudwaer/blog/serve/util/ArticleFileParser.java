package com.cloudwaer.blog.serve.util;

import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 文章文件解析：MD / TXT / PDF / DOCX → Markdown。
 *
 * @author cloudwaer
 */
@Slf4j
public final class ArticleFileParser {

	private static final long MAX_BYTES = 20L * 1024 * 1024;

	private ArticleFileParser() {
	}

	/**
	 * 解析结果
	 */
	public record ParsedArticle(String title, String content) {
	}

	/**
	 * 按扩展名解析为 Markdown
	 * @param filename 原始文件名
	 * @param bytes 文件内容
	 */
	public static ParsedArticle parse(String filename, byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			throw new BusinessException("文件内容为空");
		}
		if (bytes.length > MAX_BYTES) {
			throw new BusinessException("文件不能超过 20MB");
		}
		String safeName = filename == null ? "未命名" : filename.trim();
		String ext = extensionOf(safeName);
		String title = stripExtension(safeName);
		if (title.isBlank()) {
			title = "未命名";
		}
		String content = switch (ext) {
			case "md", "markdown", "txt" -> new String(bytes, StandardCharsets.UTF_8);
			case "pdf" -> parsePdf(bytes);
			case "docx" -> parseDocx(bytes);
			case "doc" -> throw new BusinessException("请将 .doc 另存为 .docx 或 Markdown 后导入");
			default -> throw new BusinessException(
					"不支持的文件类型：" + (ext.isBlank() ? safeName : ext) + "，仅支持 md/markdown/txt/pdf/docx");
		};
		String normalized = normalize(content);
		if (normalized.isBlank()) {
			throw new BusinessException("未能从文件中解析出文本内容");
		}
		return new ParsedArticle(title, normalized);
	}

	private static String parsePdf(byte[] bytes) {
		try (PDDocument doc = PDDocument.load(bytes)) {
			if (doc.isEncrypted()) {
				throw new BusinessException("PDF 已加密，请先解除加密后导入");
			}
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSortByPosition(true);
			String text = stripper.getText(doc);
			return textToMarkdown(text);
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (IOException e) {
			log.warn("PDF 解析失败", e);
			throw new BusinessException("PDF 解析失败：" + e.getMessage());
		}
	}

	private static String parseDocx(byte[] bytes) {
		try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
			StringBuilder sb = new StringBuilder();
			// 正文段落与表格按文档顺序由 XWPF 的 body elements 遍历
			var body = doc.getBodyElements();
			for (var element : body) {
				if (element instanceof XWPFParagraph para) {
					appendParagraph(sb, para);
				}
				else if (element instanceof XWPFTable table) {
					appendTable(sb, table);
				}
			}
			return sb.toString();
		}
		catch (IOException e) {
			log.warn("DOCX 解析失败", e);
			throw new BusinessException("Word 解析失败：" + e.getMessage());
		}
	}

	private static void appendParagraph(StringBuilder sb, XWPFParagraph para) {
		String style = para.getStyle() == null ? "" : para.getStyle().toLowerCase(Locale.ROOT);
		String text = readRuns(para).trim();
		if (text.isEmpty()) {
			return;
		}
		if (style.contains("heading1") || style.contains("标题 1") || style.equals("1")) {
			sb.append("# ").append(text).append("\n\n");
		}
		else if (style.contains("heading2") || style.contains("标题 2") || style.equals("2")) {
			sb.append("## ").append(text).append("\n\n");
		}
		else if (style.contains("heading3") || style.contains("标题 3") || style.equals("3")) {
			sb.append("### ").append(text).append("\n\n");
		}
		else if (style.contains("heading4") || style.contains("heading5") || style.contains("heading6")) {
			sb.append("#### ").append(text).append("\n\n");
		}
		else if (style.contains("list") || para.getNumID() != null) {
			sb.append("- ").append(text).append("\n");
		}
		else {
			sb.append(text).append("\n\n");
		}
	}

	private static void appendTable(StringBuilder sb, XWPFTable table) {
		sb.append("\n");
		boolean header = true;
		for (XWPFTableRow row : table.getRows()) {
			sb.append("|");
			for (XWPFTableCell cell : row.getTableCells()) {
				sb.append(' ').append(cell.getText().replace("\n", " ").trim()).append(" |");
			}
			sb.append('\n');
			if (header) {
				sb.append("|");
				for (int i = 0; i < row.getTableCells().size(); i++) {
					sb.append(" --- |");
				}
				sb.append('\n');
				header = false;
			}
		}
		sb.append('\n');
	}

	private static String readRuns(XWPFParagraph para) {
		StringBuilder sb = new StringBuilder();
		for (XWPFRun xRun : para.getRuns()) {
			String t = xRun.text();
			if (t == null) {
				continue;
			}
			if (xRun.isBold() && xRun.isItalic()) {
				sb.append("***").append(t).append("***");
			}
			else if (xRun.isBold()) {
				sb.append("**").append(t).append("**");
			}
			else if (xRun.isItalic()) {
				sb.append("*").append(t).append("*");
			}
			else {
				sb.append(t);
			}
		}
		return sb.toString();
	}

	/**
	 * 纯文本（PDF）按空行分段，段落前缀为正文
	 */
	private static String textToMarkdown(String text) {
		if (text == null) {
			return "";
		}
		String[] lines = text.split("\r?\n", -1);
		StringBuilder sb = new StringBuilder();
		StringBuilder para = new StringBuilder();
		for (String raw : lines) {
			String line = raw.stripTrailing();
			if (line.isBlank()) {
				if (para.length() > 0) {
					sb.append(para.toString().strip()).append("\n\n");
					para.setLength(0);
				}
				continue;
			}
			// 短行且无句末标点，视为标题
			if (line.length() <= 40 && !line.endsWith("。") && !line.endsWith("；") && !line.endsWith("，")
					&& !line.endsWith(".") && !line.endsWith(";") && !line.endsWith(",")) {
				if (para.length() > 0) {
					sb.append(para.toString().strip()).append("\n\n");
					para.setLength(0);
				}
				sb.append("## ").append(line.strip()).append("\n\n");
				continue;
			}
			if (para.length() > 0) {
				para.append(' ');
			}
			para.append(line.strip());
		}
		if (para.length() > 0) {
			sb.append(para.toString().strip()).append("\n\n");
		}
		return sb.toString();
	}

	private static String normalize(String content) {
		if (content == null) {
			return "";
		}
		// 统一换行，去掉多余连续空行
		String s = content.replace("\r\n", "\n").replace('\r', '\n');
		s = s.replaceAll("\n{3,}", "\n\n");
		return s.strip();
	}

	private static String extensionOf(String filename) {
		int dot = filename.lastIndexOf('.');
		if (dot < 0 || dot == filename.length() - 1) {
			return "";
		}
		return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
	}

	private static String stripExtension(String filename) {
		int dot = filename.lastIndexOf('.');
		if (dot <= 0) {
			return filename;
		}
		return filename.substring(0, dot).trim();
	}

}
