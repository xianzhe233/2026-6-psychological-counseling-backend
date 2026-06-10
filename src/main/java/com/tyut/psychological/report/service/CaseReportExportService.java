package com.tyut.psychological.report.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.service.CounselorAccessService;
import com.tyut.psychological.profile.entity.StaffProfile;
import com.tyut.psychological.report.mapper.CaseReportMapper;
import com.tyut.psychological.report.vo.CaseReportExportResult;
import com.tyut.psychological.report.vo.CaseReportExportVO;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class CaseReportExportService {
    private final CaseReportMapper caseReportMapper;
    private final CounselorAccessService counselorAccessService;
    private final OperationLogService operationLogService;

    public CaseReportExportService(CaseReportMapper caseReportMapper,
                                   CounselorAccessService counselorAccessService,
                                   OperationLogService operationLogService) {
        this.caseReportMapper = caseReportMapper;
        this.counselorAccessService = counselorAccessService;
        this.operationLogService = operationLogService;
    }

    public CaseReportExportResult exportForCounselor(Long counselorUserId, Long reportId) {
        StaffProfile counselor = counselorAccessService.requireCounselorStaff(counselorUserId);
        CaseReportExportVO report = caseReportMapper.selectExportForCounselor(reportId, counselor.getId());
        if (report == null) {
            throw new BusinessException(404, "结案报告不存在或无权下载");
        }
        CaseReportExportResult result = buildWord(report);
        operationLogService.logSuccess("结案报告", "导出结案报告",
                "报告ID: " + reportId + "，学生: " + report.getStudentName());
        return result;
    }

    public CaseReportExportResult exportForAdmin(Long reportId) {
        CaseReportExportVO report = caseReportMapper.selectExportForAdmin(reportId);
        if (report == null) {
            throw new BusinessException(404, "已提交的结案报告不存在");
        }
        CaseReportExportResult result = buildWord(report);
        operationLogService.logSuccess("结案报告", "导出结案报告",
                "管理员导出报告ID: " + reportId + "，学生: " + report.getStudentName());
        return result;
    }

    private CaseReportExportResult buildWord(CaseReportExportVO report) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("心理咨询结案报告");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("宋体");

            document.createParagraph();

            XWPFTable table = document.createTable(8, 2);
            table.setWidth("100%");
            fillRow(table.getRow(0), "学号", defaultText(report.getStudentNo()));
            fillRow(table.getRow(1), "姓名", defaultText(report.getStudentName()));
            fillRow(table.getRow(2), "性别", defaultText(report.getGender()));
            fillRow(table.getRow(3), "院系", defaultText(report.getCollege()));
            fillRow(table.getRow(4), "联系电话", defaultText(report.getPhone()));
            fillRow(table.getRow(5), "问题类型", defaultText(report.getProblemTypeLabel()));
            fillRow(table.getRow(6), "咨询总次数", report.getTotalSessions() == null ? "—" : report.getTotalSessions().toString());
            fillRow(table.getRow(7), "咨询效果自评", defaultText(report.getEffectSelfRating()));

            document.createParagraph();
            appendSection(document, "个案总结", report.getCaseSummary());
            appendSection(document, "后续建议", report.getSuggestion());

            document.write(outputStream);
            String fileName = defaultText(report.getStudentName()) + "-结案报告.docx";
            return new CaseReportExportResult(fileName, outputStream.toByteArray());
        } catch (IOException ex) {
            throw new BusinessException(500, "生成 Word 报告失败");
        }
    }

    private void fillRow(XWPFTableRow row, String label, String value) {
        setCellText(row.getCell(0), label, true);
        setCellText(row.getCell(1), value, false);
    }

    private void appendSection(XWPFDocument document, String title, String content) {
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(14);
        titleRun.setFontFamily("宋体");

        XWPFParagraph contentParagraph = document.createParagraph();
        XWPFRun contentRun = contentParagraph.createRun();
        contentRun.setText(defaultText(content));
        contentRun.setFontSize(12);
        contentRun.setFontFamily("宋体");
        document.createParagraph();
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontFamily("宋体");
        run.setFontSize(12);
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "—" : value.trim();
    }
}
