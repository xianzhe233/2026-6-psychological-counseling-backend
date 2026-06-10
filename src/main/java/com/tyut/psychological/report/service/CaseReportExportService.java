package com.tyut.psychological.report.service;

import com.tyut.psychological.common.exception.BusinessException;
import com.tyut.psychological.common.log.service.OperationLogService;
import com.tyut.psychological.consultation.service.CounselorAccessService;
import com.tyut.psychological.report.mapper.CaseReportMapper;
import com.tyut.psychological.report.vo.CaseReportExportVO;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
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

    public CaseReportExportVO fetchForCounselor(Long counselorUserId, Long reportId) {
        counselorAccessService.requireCounselorStaff(counselorUserId);
        CaseReportExportVO r = caseReportMapper.selectExportForCounselor(reportId, counselorUserId);
        if (r == null) throw new BusinessException(404, "结案报告不存在或无权下载");
        return r;
    }

    public CaseReportExportVO fetchForAdmin(Long reportId) {
        CaseReportExportVO r = caseReportMapper.selectExportForAdmin(reportId);
        if (r == null) throw new BusinessException(404, "已提交的结案报告不存在");
        return r;
    }

    public byte[] buildWord(CaseReportExportVO report) {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun tr = title.createRun();
            tr.setText("心理咨询结案报告"); tr.setBold(true); tr.setFontSize(20); tr.setFontFamily("宋体");

            XWPFParagraph sub = doc.createParagraph();
            sub.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun sr = sub.createRun();
            sr.setText("（内部资料 请勿外传）"); sr.setFontSize(10); sr.setColor("999999");

            doc.createParagraph();
            XWPFTable table = doc.createTable(8, 2);
            setTableBorders(table);
            fillRow(table.getRow(0), "学号", v(report.getStudentNo()));
            fillRow(table.getRow(1), "姓名", v(report.getStudentName()));
            fillRow(table.getRow(2), "性别", v(report.getGender()));
            fillRow(table.getRow(3), "院系", v(report.getCollege()));
            fillRow(table.getRow(4), "联系电话", v(report.getPhone()));
            fillRow(table.getRow(5), "问题类型", v(report.getProblemTypeLabel()));
            fillRow(table.getRow(6), "咨询总次数", report.getTotalSessions() == null ? "—" : report.getTotalSessions().toString());
            fillRow(table.getRow(7), "咨询效果自评", v(report.getEffectSelfRating()));
            doc.createParagraph();
            addSection(doc, "个案总结", report.getCaseSummary());
            addSection(doc, "后续建议", report.getSuggestion());
            doc.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "生成 Word 报告失败");
        }
    }

    public void logExport(String role, Long reportId, String studentName) {
        operationLogService.logSuccess("结案报告", "导出结案报告",
                role + "导出报告ID: " + reportId + "，学生: " + studentName);
    }

    private void fillRow(XWPFTableRow row, String label, String value) {
        XWPFTableCell left = row.getCell(0);
        left.getCTTc().addNewTcPr().addNewShd().setFill("F0F0F0");
        setCell(left, label, true);
        setCell(row.getCell(1), value, false);
    }

    private void addSection(XWPFDocument doc, String title, String content) {
        XWPFParagraph tp = doc.createParagraph();
        XWPFRun tr = tp.createRun();
        tr.setText("▎" + title); tr.setBold(true); tr.setFontSize(14); tr.setFontFamily("宋体");
        XWPFParagraph cp = doc.createParagraph();
        XWPFRun cr = cp.createRun();
        cr.setText(v(content)); cr.setFontSize(12); cr.setFontFamily("宋体");
        doc.createParagraph();
    }

    private void setCell(XWPFTableCell cell, String text, boolean bold) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        XWPFRun r = p.createRun();
        r.setText(text); r.setBold(bold); r.setFontFamily("宋体"); r.setFontSize(11);
    }

    private void setTableBorders(XWPFTable table) {
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders borders =
                table.getCTTbl().getTblPr() != null && table.getCTTbl().getTblPr().isSetTblBorders()
                        ? table.getCTTbl().getTblPr().getTblBorders()
                        : table.getCTTbl().addNewTblPr().addNewTblBorders();
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    private String v(String s) { return s == null || s.isBlank() ? "—" : s.trim(); }
}
