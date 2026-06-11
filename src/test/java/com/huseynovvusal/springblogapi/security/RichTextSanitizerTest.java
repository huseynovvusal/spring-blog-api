package com.huseynovvusal.springblogapi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RichTextSanitizerTest {

  private final RichTextSanitizer sanitizer = new RichTextSanitizer();

  @Test
  void shouldRemoveScriptTag() {
    String raw = "<script>alert('XSS')</script>";
    String sanitized = sanitizer.sanitize(raw);

    assertFalse(sanitized.contains("<script>"));
  }

  @Test
  void shouldRemoveNestedScriptTag() {
    String raw = "<scr<script>ipt>alert('xss')</scr<script>ipt>";
    String sanitized = sanitizer.sanitize(raw);

    assertFalse(sanitized.contains("<script>"));
  }

  @Test
  void shouldRemoveCapitalScriptTag() {
    String raw = "<SCRIPT>alert('XSS')</SCRIPT>";
    String sanitized = sanitizer.sanitize(raw);

    assertFalse(sanitized.contains("<SCRIPT>"));
  }

  @Test
  void shouldRemoveJavascriptLink() {
    String raw = "<a href=\"javascript:alert('xss')\">clique</a>";
    String sanitized = sanitizer.sanitize(raw);

    assertFalse(sanitized.contains("javascript"));
  }

  @Test
  void shouldPreserveContent() {
    String raw = "<b>Hello</b> <i>World</i>";
    String sanitized = sanitizer.sanitize(raw);

    assertEquals(raw, sanitized);
  }

  @Test
  void shouldPreserveLink() {
    String raw = "<a href=\"https://google.com\">Google</a>";
    String sanitized = sanitizer.sanitize(raw);

    assertTrue(sanitized.contains("<a href=\"https://google.com\" rel=\"nofollow\">Google</a>"));
  }

  @Test
  void shouldRemoveImg() {
    String raw = "<img src=x onerror=alert(1)>";
    String sanitized = sanitizer.sanitize(raw);

    assertFalse(sanitized.contains("<img"));
    assertFalse(sanitized.contains("onerror"));
  }
}
