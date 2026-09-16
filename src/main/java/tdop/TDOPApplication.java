package tdop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
                Dotenv dotenv = Dotenv.configure()
                        .directory(envPath.getParent().toString())
                        .filename(".env")
                        .load();

                dotenv.entries().forEach(entry -> {
                    if (System.getenv(entry.getKey()) == null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                        System.setProperty(entry.getKey(), entry.getValue());
                    }
                });
                System.out.println("Loaded .env from: " + envPath.toAbsolutePath());
            } else {
                System.err.println("Warning: .env file not found. Using system environment variables.");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }

        SpringApplication.run(TDOPApplication.class, args);
    }
}
