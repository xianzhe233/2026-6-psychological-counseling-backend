package com.tyut.psychological.report.vo;

public class CaseReportExportResult {
    private final String fileName;
    private final byte[] content;

    public CaseReportExportResult(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() { return fileName; }
    public byte[] getContent() { return content; }
}
