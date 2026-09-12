package tdop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_profile_id")
    private SeekerProfile seekerProfile;
}
