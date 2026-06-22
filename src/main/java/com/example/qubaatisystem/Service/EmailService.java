package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Model.Payment;
import com.example.qubaatisystem.Model.Subscription;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Value("${mail.from:}")
    private String from;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPaymentConfirmation(Payment payment) {
        if (from == null || from.isBlank()) {
            log.warn("Payment confirmation email skipped — MAIL_FROM is not configured (ref={})",
                    payment.getLocalReference());
            return;
        }

        String to = resolveRecipientEmail(payment);
        if (to == null || to.isBlank()) {
            log.warn("Payment confirmation email skipped — recipient email could not be resolved (ref={})",
                    payment.getLocalReference());
            return;
        }

        try {
            Subscription sub      = payment.getSubscription();
            String planName       = escape(payment.getPlan().getName());
            String amountSar      = escape(String.format("%.2f ريال", payment.getAmount() / 100.0));
            String localReference = escape(payment.getLocalReference());
            String paidAt         = escape(payment.getPaidAt() != null ? payment.getPaidAt().format(DATE_FMT) : "—");
            String endsAt         = escape(sub != null && sub.getEndsAt() != null ? sub.getEndsAt().format(DATE_FMT) : "—");

            String html = buildPaymentConfirmationTemplate(planName, amountSar, localReference, paidAt, endsAt);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("تم تأكيد اشتراكك — " + payment.getPlan().getName());
            helper.setText(html, true);
            mailSender.send(mime);

            log.info("Payment confirmation email sent to={} ref={}", to, payment.getLocalReference());
        } catch (Exception e) {
            log.warn("Failed to send payment confirmation email ref={}: {}",
                    payment.getLocalReference(), e.getMessage());
        }
    }

    // ---------- helpers ----------

    private String resolveRecipientEmail(Payment payment) {
        if (payment.getParent() != null && payment.getParent().getUser() != null) {
            return payment.getParent().getUser().getEmail();
        }
        if (payment.getTeacher() != null && payment.getTeacher().getUser() != null) {
            return payment.getTeacher().getUser().getEmail();
        }
        return null;
    }

    private String escape(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String buildPaymentConfirmationTemplate(
            String planName, String amountSar, String localReference,
            String paidAt, String endsAt) {

        return """
                <div dir="rtl" style="font-family: Arial, sans-serif; background-color:#FAF8F4; padding:32px;">
                    <div style="max-width:620px; margin:auto; background-color:#ffffff; border-radius:16px; overflow:hidden; border:1px solid #d8d0c4;">

                        <div style="background-color:#ffffff; padding:28px 28px 12px; text-align:right;">
                            <h1 style="margin:0; color:#0B3A3A; font-size:40px; font-weight:bold;">قبعتي</h1>
                            <p style="margin:6px 0 0; color:#7FA396; font-size:15px;">منصة التعلم التفاعلي للأطفال</p>
                        </div>

                        <div style="height:6px; background-color:#F5B800;"></div>

                        <div style="padding:30px 28px; text-align:right;">
                            <h2 style="color:#F27548; font-size:22px; margin:0 0 24px;">
                                🎉 تم تأكيد اشتراكك بنجاح!
                            </h2>

                            <table style="width:100%%; border-collapse:collapse; font-size:15px; color:#333333; line-height:1.8;">
                                <tr style="border-bottom:1px solid #f0ebe3;">
                                    <td style="padding:10px 4px; color:#7FA396; width:45%%;">اسم الخطة</td>
                                    <td style="padding:10px 4px; font-weight:bold; color:#0B3A3A;">%s</td>
                                </tr>
                                <tr style="border-bottom:1px solid #f0ebe3;">
                                    <td style="padding:10px 4px; color:#7FA396;">المبلغ المدفوع</td>
                                    <td style="padding:10px 4px; font-weight:bold; color:#0B3A3A;">%s</td>
                                </tr>
                                <tr style="border-bottom:1px solid #f0ebe3;">
                                    <td style="padding:10px 4px; color:#7FA396;">رقم المرجع</td>
                                    <td style="padding:10px 4px; font-family:monospace; font-size:13px; color:#7563B6;">%s</td>
                                </tr>
                                <tr style="border-bottom:1px solid #f0ebe3;">
                                    <td style="padding:10px 4px; color:#7FA396;">تاريخ الدفع</td>
                                    <td style="padding:10px 4px; color:#333333;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding:10px 4px; color:#7FA396;">صلاحية الاشتراك حتى</td>
                                    <td style="padding:10px 4px; font-weight:bold; color:#F27548;">%s</td>
                                </tr>
                            </table>
                        </div>

                        <div style="background-color:#FAF8F4; padding:18px 28px; text-align:center; border-top:1px solid #f0ebe3;">
                            <p style="font-size:12px; color:#7FA396; margin:0;">
                                - هذه رسالة تلقائية من منصة قبعتي، يُرجى عدم الرد عليها -
                            </p>
                        </div>
                    </div>
                </div>
                """.formatted(planName, amountSar, localReference, paidAt, endsAt);
    }
}
