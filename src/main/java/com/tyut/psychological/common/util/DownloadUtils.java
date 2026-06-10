package com.tyut.psychological.common.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class DownloadUtils {
    private DownloadUtils() {}

    public static void writeAttachment(HttpServletResponse response, String fileName, byte[] content) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8).build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
        response.flushBuffer();
    }
}
