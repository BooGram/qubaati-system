package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.ChildLearningProfileOutDTO;
import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentPortfolioPdfService {

    private static final String TEMPLATE_PATH = "templates/student-portfolio-report.html";
    private static final String FONT_PATH = "static/fonts/Tajawal.ttf";
    private static final String LOGO_PATH = "static/images/qubaati-logo.png";
    private static final String MARK_PATH = "static/images/qubaati-mark.png";
    private static final String IDENTITY_PATH = "static/images/qubaati-identity.png";
    private static final String REPORT_BANNER_PATH = "static/images/qubaati-report-banner.png";
    private static final String REPORT_STRIP_PATH = "static/images/qubaati-report-strip.png";

    private final ChildLearningProfileService childLearningProfileService;
    private final com.example.qubaatisystem.Config.SecurityOwnershipService security;

    public byte[] generateChildPortfolio(Integer parentId, Integer studentId) {
        ChildLearningProfileOutDTO profile = childLearningProfileService.getLearningProfile(parentId, studentId);
        return generatePortfolioPdf(profile);
    }

    public byte[] generateTeacherStudentPortfolio(Integer teacherId, Integer studentId) {
        ChildLearningProfileOutDTO profile = childLearningProfileService.getTeacherLearningProfile(teacherId, studentId);
        return generatePortfolioPdf(profile);
    }

    // Secure wrapper: teacher from Basic Auth, must own the target student (student in the teacher's classroom).
    public byte[] generateMyStudentPortfolio(com.example.qubaatisystem.Model.User user,
                                             com.example.qubaatisystem.DTO.In.StudentTargetInDTO dto) {
        security.assertTeacherCanAssignToStudent(user, dto.getStudentId());
        return generateTeacherStudentPortfolio(security.getCurrentTeacherId(user), dto.getStudentId());
    }

    public byte[] generatePortfolioPdf(ChildLearningProfileOutDTO profile) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String html = buildHtml(profile);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
            builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
            builder.withHtmlContent(html, classpathBaseUri());
            addTajawalFont(builder);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("Unable to generate student portfolio PDF", e);
            throw new ApiException("Unable to generate student portfolio PDF", e);
        }
    }

    private String buildHtml(ChildLearningProfileOutDTO profile) throws IOException {
        String html = readTextResource(TEMPLATE_PATH);
        return html
                .replace("{{reportDate}}", escape(LocalDate.now().toString()))
                .replace("{{logoDataUri}}", imageDataUri(LOGO_PATH))
                .replace("{{markDataUri}}", imageDataUri(MARK_PATH))
                .replace("{{identityDataUri}}", imageDataUri(IDENTITY_PATH))
                .replace("{{reportBannerDataUri}}", imageDataUri(REPORT_BANNER_PATH))
                .replace("{{reportStripDataUri}}", imageDataUri(REPORT_STRIP_PATH))
                .replace("{{studentName}}", escape(value(profile.getFullName())))
                .replace("{{grade}}", escape(value(profile.getGrade())))
                .replace("{{age}}", escape(value(profile.getAge())))
                .replace("{{totalPoints}}", escape(value(profile.getTotalPoints())))
                .replace("{{completedMissions}}", escape(value(profile.getCompletedMissionsCount())))
                .replace("{{completedMissionSessions}}", escape(value(profile.getCompletedMissionSessionsCount())))
                .replace("{{averageActivityScore}}", escape(percent(profile.getAverageActivityScore())))
                .replace("{{learningStyle}}", escape(translateLearningStyle(profile.getLearningStylePrimary())))
                .replace("{{skillsHtml}}", skillsHtml(profile.getSkills()))
                .replace("{{recommendationsHtml}}", recommendationsHtml(profile.getTopRecommendations()))
                .replace("{{insightSummary}}", escape(value(profile.getLatestMissionInsightSummary())))
                .replace("{{insightRecommendation}}", escape(value(profile.getLatestMissionInsightRecommendation())));
    }

    private String skillsHtml(List<ChildLearningProfileOutDTO.SkillRow> skills) {
        if (skills == null || skills.isEmpty()) {
            return "<p>لم يتم تسجيل مؤشرات مهارية بعد.</p>";
        }

        StringBuilder html = new StringBuilder();
        for (ChildLearningProfileOutDTO.SkillRow skill : skills) {
            double score = normalized(skill.getScore());
            String levelClass = score < 60 ? "low" : score < 80 ? "mid" : "hi";
            html.append("<table class=\"skill\"><tr><td>")
                    .append("<div class=\"sk-line\">")
                    .append("<span class=\"sk-name\">").append(escape(displaySkillName(skill.getSkillName()))).append("</span>")
                    .append("<span class=\"sk-pct pct-").append(levelClass).append("\">").append(escape(percent(skill.getScore()))).append("</span>")
                    .append("</div>")
                    .append("<div class=\"barwrap\"><div class=\"fill fill-").append(levelClass)
                    .append("\" style=\"width: ").append(score).append("%\"></div></div>")
                    .append("<div class=\"sk-meta\">المستوى الحالي: ").append(escape(value(skill.getLevel()))).append("</div>")
                    .append("</td></tr></table>");
        }
        return html.toString();
    }

    private String displaySkillName(String skillName) {
        String value = value(skillName);
        return value.replaceAll("\\s*\\d{8,}\\s*", " ").trim();
    }

    private String recommendationsHtml(List<String> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return "<div class=\"reco-row\"><span class=\"reco-dot\"></span><span class=\"reco-text\">لا توجد توصيات متاحة حاليًا.</span></div>";
        }

        StringBuilder html = new StringBuilder();
        for (String recommendation : recommendations) {
            html.append("<div class=\"reco-row\"><span class=\"reco-dot\"></span><span class=\"reco-text\">")
                    .append(escape(recommendation))
                    .append("</span></div>");
        }
        return html.toString();
    }

    private void addTajawalFont(PdfRendererBuilder builder) throws IOException {
        ClassPathResource fontResource = new ClassPathResource(FONT_PATH);
        if (!fontResource.exists()) {
            return;
        }

        File fontFile = fontResource.getFile();
        builder.useFont(fontFile, "Tajawal");
    }

    private String readTextResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private String imageDataUri(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return "";
        }
        String contentType = path.endsWith(".png") ? "image/png" : "image/jpeg";
        byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    private String classpathBaseUri() throws IOException {
        return new ClassPathResource("templates/").getURL().toString();
    }

    private double normalized(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(100.0, value));
    }

    private String value(Object value) {
        return value == null ? "غير متوفر" : String.valueOf(value);
    }

    private String percent(Double value) {
        return value == null ? "غير متوفر" : Math.round(value) + "%";
    }

    private String translateLearningStyle(String value) {
        if (value == null) {
            return "غير متوفر";
        }
        return switch (value) {
            case "VISUAL" -> "بصري";
            case "AUDITORY" -> "سمعي";
            case "READING_WRITING" -> "قراءة وكتابة";
            case "KINESTHETIC" -> "حركي وتجريبي";
            default -> value;
        };
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
