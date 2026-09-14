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

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private tdop.entity.enums.ProfileVisibility profileVisibility = tdop.entity.enums.ProfileVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private tdop.entity.enums.NotificationPreference notificationPreference = tdop.entity.enums.NotificationPreference.ALL;

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "seekerProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CareerGoal> careerGoals = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
