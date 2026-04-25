package com.example.edu_project.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * HTML 内容 sanititizer 工具类
 * 用于防止 XSS 攻击，对用户输入的 HTML 内容进行过滤
 */
@Component
public class HtmlSanitizer {

    /**
     * 宽松白名单：允许部分 HTML 标签（用于文章内容等富文本）
     * 允许：p, br, h1-h6, ul, ol, li, em, strong, a, img, blockquote, code, pre
     */
    private static final Safelist RELAXED_WHITELIST = Safelist.relaxed()
            .addTags("span", "div", "hr", "table", "thead", "tbody", "tr", "th", "td")
            .addAttributes("a", "href", "title", "target")
            .addAttributes("img", "src", "alt", "title", "width", "height")
            .addProtocols("img", "src", "http", "https")
            .addProtocols("a", "href", "http", "https", "mailto")
            .preserveRelativeLinks(false);

    /**
     * 严格白名单：只允许纯文本（用于评论内容等）
     * 移除所有 HTML 标签，只保留纯文本
     */
    private static final Safelist STRICT_WHITELIST = Safelist.none();

    /**
     * 过滤富文本内容（文章标题、摘要、内容）
     * 保留基本格式标签，移除危险标签和脚本
     *
     * @param html 未过滤的 HTML 内容
     * @return 过滤后的安全 HTML 内容
     */
    public String sanitizeRichText(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return Jsoup.clean(html, RELAXED_WHITELIST);
    }

    /**
     * 过滤纯文本内容（评论内容等）
     * 完全移除所有 HTML 标签，只保留纯文本
     *
     * @param text 未过滤的文本内容
     * @return 过滤后的纯文本
     */
    public String sanitizePlainText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Jsoup.clean(text, STRICT_WHITELIST);
    }

    /**
     * 检查内容是否包含潜在危险标签
     *
     * @param html 待检查的 HTML 内容
     * @return true 如果包含危险标签
     */
    public boolean containsDangerousTags(String html) {
        if (html == null || html.isEmpty()) {
            return false;
        }
        String sanitized = Jsoup.clean(html, RELAXED_WHITELIST);
        // 如果 sanitized 与原内容差异过大，说明包含危险内容
        return sanitized.length() < html.length() * 0.5;
    }
}
