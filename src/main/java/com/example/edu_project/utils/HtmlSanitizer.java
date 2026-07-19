package com.example.edu_project.utils;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class HtmlSanitizer {

    private final AntiSamy antiSamy;
    private final Policy policy;

    public HtmlSanitizer() {
        try {
            InputStream is = new ClassPathResource("antisamy.xml").getInputStream();
            policy = Policy.getInstance(is);
            antiSamy = new AntiSamy();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize HtmlSanitizer", e);
        }
    }

    public String sanitizeRichText(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        try {
            CleanResults results = antiSamy.scan(html, policy);
            return results.getCleanHTML();
        } catch (Exception e) {
            // Fallback: strip all HTML tags
            return html.replaceAll("<[^>]*>", "");
        }
    }

    public String sanitizePlainText(String text) {
        if (text == null) {
            return null;
        }
        // For plain text, escape HTML entities
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}