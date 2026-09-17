package tdop.notification.email;

public class EmailTemplate {

    public static String buildVerificationEmail(String orgName, String link) {
        return "<html><body>"
            + "<h1>Verification Request</h1>"
            + "<p>Dear " + orgName + ",</p>"
            + "<p>Please verify your account: <a href=\"" + link + "\">Verify</a></p>"
            + "</body></html>";
    }

    public static String buildApplicationEmail(String oppTitle, String applicantName) {
        return "<html><body>"
            + "<h1>New Application</h1>"
            + "<p>You received a new application from " + applicantName
            + " for " + oppTitle + "</p>"
            + "</body></html>";
    }

    public static String buildNotificationEmail(String title, String message) {
        return "<html><body>"
            + "<h1>" + title + "</h1>"
            + "<p>" + message + "</p>"
            + "</body></html>";
    }

    public static String buildWelcomeEmail(String fullName) {
        return "<html><body>"
            + "<h1>Welcome to TDOP!</h1>"
            + "<p>Hi " + fullName + ",</p>"
            + "<p>Thank you for joining the Talent Development Opportunity Platform.</p>"
            + "<p>Start exploring opportunities that match your skills and goals.</p>"
            + "</body></html>";
    }

    public static String buildApplicationStatusEmail(String oppTitle, String status) {
        return "<html><body>"
            + "<h1>Application Status Update</h1>"
            + "<p>Your application for <strong>" + oppTitle + "</strong> has been updated to: <strong>" + status + "</strong></p>"
            + "<p>Log in to your dashboard for more details.</p>"
            + "</body></html>";
    }

    public static String buildPasswordResetEmail(String link) {
        return "<html><body>"
            + "<h1>Password Reset</h1>"
            + "<p>You requested a password reset. Click the link below to reset your password:</p>"
            + "<p><a href=\"" + link + "\">Reset Password</a></p>"
            + "<p>If you didn't request this, please ignore this email.</p>"
            + "</body></html>";
    }
}
