package com.huseynovvusal.springblogapi.security;

import org.springframework.stereotype.Component;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Utility class for sanitizing user-provided rich text content to prevent XSS attacks.
 * Uses the OWASP Java HTML Sanitizer to strip disallowed tags and attributes.
 * Disallowed tags are removed while preserving safe textual content when possible.
 * 
 * Allowed elements: p, br, b, strong, i, em, ul, ol, li, blockquote, pre, a
 * Allowed on links: href (http/https only), rel="nofollow" enforced automatically.
 * 
 */
@Component
public class RichTextSanitizer {
    private static final PolicyFactory policy = new HtmlPolicyBuilder()
    .allowElements("p", "br", "b", "strong", "i", "em", "ul", "ol", "li", "blockquote", "a", "pre")
    .allowAttributes("href").onElements("a")
    .allowUrlProtocols("http", "https")
    .requireRelNofollowOnLinks()
    .toFactory();

    /**
     * Returns sanitized HTML, or null if input is null.
     * All tags and attributes not in the policy are stripped.
     *
     * @param content raw HTML string to sanitize
     * @return sanitized HTML, or null if content is null
     */
    public String sanitize(String content){
        if (content!=null) {
            return policy.sanitize(content);
        }
        return null;
    }
}