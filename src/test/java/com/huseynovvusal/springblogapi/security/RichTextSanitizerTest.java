package com.huseynovvusal.springblogapi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RichTextSanitizerTest {

    private final RichTextSanitizer sanitizer = new RichTextSanitizer();

    @Test
    void should_remove_script_tag(){
        String raw = "<script>alert('XSS')</script>";
        String sanitized = sanitizer.sanitize(raw);

        assertFalse(sanitized.contains("<script>"));
    }

    @Test
    void should_remove_nested_script_tag(){
        String raw = "<scr<script>ipt>alert('xss')</scr<script>ipt>";
        String sanitized = sanitizer.sanitize(raw);
        
        assertFalse(sanitized.contains("<script>"));
    }

    @Test
    void should_remove_capital_script_tag(){
        String raw = "<SCRIPT>alert('XSS')</SCRIPT>";
        String sanitized = sanitizer.sanitize(raw);

        assertFalse(sanitized.contains("<SCRIPT>"));
    }

    @Test
    void should_remove_javascript_link(){
        String raw = "<a href=\"javascript:alert('xss')\">clique</a>";
        String sanitized = sanitizer.sanitize(raw);

        assertFalse(sanitized.contains("javascript"));
    }

    @Test
    void should_preserve_content(){
        String raw = "<b>Hello</b> <i>World</i>";
        String sanitized = sanitizer.sanitize(raw);

        assertEquals(raw, sanitized);
    }

    @Test
    void should_preserve_link(){
        String raw = "<a href=\"https://google.com\">Google</a>";
        String sanitized = sanitizer.sanitize(raw);

        assertTrue(sanitized.contains("<a href=\"https://google.com\" rel=\"nofollow\">Google</a>"));
    }

    @Test
    void should_remove_img(){
        String raw = "<img src=x onerror=alert(1)>";
        String sanitized = sanitizer.sanitize(raw);

        assertFalse(sanitized.contains("<img"));
        assertFalse(sanitized.contains("onerror"));
    }
}