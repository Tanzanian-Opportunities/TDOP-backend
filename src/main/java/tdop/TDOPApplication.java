package tdop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class TDOPApplication {
    public static void main(String[] args) {
        try {
            String userDir = System.getProperty("user.dir");
            Path envPath = Paths.get(userDir, ".env");

            if (!Files.exists(envPath)) {
                Path classDir = Paths.get(TDOPApplication.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI()).getParent();
                if (classDir != null) {
                    envPath = classDir.resolve(".env");
                    if (!Files.exists(envPath)) {
                        envPath = classDir.getParent().resolve(".env");
                    }
                }
            }

            if (Files.exists(envPath)) {
                try (BufferedReader reader = Files.newBufferedReader(envPath)) {
                    String line;
                    int count = 0;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;

                        int eq = line.indexOf('=');
                        if (eq <= 0) continue;

                        String key = line.substring(0, eq).trim();
                        String value = line.substring(eq + 1).trim();

                        if (System.getenv(key) == null && !value.isEmpty()) {
                            System.setProperty(key, value);
                            count++;
                        }
                    }
                    System.out.println("Loaded " + count + " env vars from: " + envPath.toAbsolutePath());
                }
            } else {
                System.err.println("Warning: .env file not found. Using system environment variables.");
                System.err.println("  Searched: " + envPath.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }

        SpringApplication.run(TDOPApplication.class, args);
    }
}
