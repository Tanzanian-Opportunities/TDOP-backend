package tdop.entity;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum ApplicationStatus {
    PENDING, REVIEWED, INTERVIEW, OFFER, REJECTED, APPLIED
}
