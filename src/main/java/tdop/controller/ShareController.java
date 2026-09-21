package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.entity.Opportunity;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class ShareController {

    private final OpportunityRepository opportunityRepository;

    @GetMapping("/{id}/share")
    public ResponseEntity<Map<String, String>> shareLink(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        String appUrl = System.getenv("FRONTEND_URL") != null
            ? System.getenv("FRONTEND_URL") : "https://tdop.co.tz";
        String shareUrl = appUrl + "/opportunities/" + id;
        String shareText = "Check out this opportunity: " + opp.getTitle() +
            (opp.getLocation() != null ? " in " + opp.getLocation() : "");

        return ResponseEntity.ok(Map.of(
            "url", shareUrl,
            "text", shareText,
            "whatsapp", "https://wa.me/?text=" + encode(shareText + " " + shareUrl),
            "twitter", "https://twitter.com/intent/tweet?text=" + encode(shareText) + "&url=" + encode(shareUrl),
            "linkedin", "https://www.linkedin.com/sharing/share-offsite/?url=" + encode(shareUrl),
            "facebook", "https://www.facebook.com/sharer/sharer.php?u=" + encode(shareUrl)
        ));
    }

    private String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}
