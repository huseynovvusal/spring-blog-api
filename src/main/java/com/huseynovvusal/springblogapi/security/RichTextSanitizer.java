package com.huseynovvusal.springblogapi.security;

import org.springframework.stereotype.Component;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

@Component
public class RichTextSanitizer {
    private static final PolicyFactory policy = new HtmlPolicyBuilder()
    .allowElements("p", "br", "b", "strong", "i", "em", "ul", "ol", "li", "blockquote", "a", "pre")
    .allowAttributes("href").onElements("a")
    .allowUrlProtocols("http", "https")
    .requireRelNofollowOnLinks()
    .toFactory();

    public String sanitize(String content){
        if (content!=null) {
            return policy.sanitize(content);
        }
        return null;
    }
}