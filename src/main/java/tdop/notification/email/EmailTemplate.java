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
}
