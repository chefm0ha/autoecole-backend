package com.autoecole.scheduler;

import com.autoecole.enums.ExamStatus;
import com.autoecole.model.Exam;
import com.autoecole.repository.ExamDao;
import com.autoecole.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ExamReminderScheduler {

    private final ExamDao examDao;
    private final NotificationService notificationService;

    /**
     * Check for exams that are 5 days away and send reminders
     * Runs every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkExamsAndSendReminders() {
        log.info("Starting exam reminder check...");

        try {
            // Calculate the target date (5 days from today)
            LocalDate targetDate = LocalDate.now().plusDays(5);

            // Find all scheduled exams for that date
            List<Exam> upcomingExams = examDao.findExamsByDate(targetDate);

            if (upcomingExams.isEmpty()) {
                log.info("No exams found for date: {}", targetDate);
                return;
            }

            log.info("Found {} exam(s) scheduled for {}", upcomingExams.size(), targetDate);

            // Create notifications for each exam (with user_id = null for broadcast)
            int notificationsCreated = 0;

            for (Exam exam : upcomingExams) {
                // Only send reminders for scheduled exams
                if (exam.getStatus() != ExamStatus.SCHEDULED) {
                    log.debug("Skipping exam {} as it's not scheduled (status: {})",
                            exam.getId(), exam.getStatus());
                    continue;
                }

                try {
                    // Create notification with user_id = null (broadcast to all staff/admin)
                    var notification = notificationService.createExamReminderNotification(exam);
                    if (notification != null) {
                        // Send broadcast notification to all staff/admin
                        notificationService.sendBroadcastNotification(notification);
                        notificationsCreated++;
                    }
                } catch (Exception e) {
                    log.error("Failed to create notification for exam {}: {}",
                            exam.getId(), e.getMessage(), e);
                }
            }

            log.info("Exam reminder check completed. Created {} notifications for {} exams",
                    notificationsCreated, upcomingExams.size());

        } catch (Exception e) {
            log.error("Error during exam reminder check: {}", e.getMessage(), e);
        }
    }

    /**
     * Retry failed WhatsApp notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    public void retryFailedWhatsAppNotifications() {
        log.debug("Checking for failed WhatsApp notifications to retry...");

        try {
            notificationService.retryFailedNotifications();
        } catch (Exception e) {
            log.error("Error during WhatsApp retry: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up old notifications
     * Runs every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldNotifications() {
        log.debug("Cleaning up old notifications...");

        try {
            notificationService.cleanupOldNotifications();
        } catch (Exception e) {
            log.error("Error during notification cleanup: {}", e.getMessage(), e);
        }
    }
}