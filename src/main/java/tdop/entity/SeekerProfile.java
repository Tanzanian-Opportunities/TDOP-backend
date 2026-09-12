package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seeker_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String bio;
    private String location;
    private String profilePicture;

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interest> interests = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
