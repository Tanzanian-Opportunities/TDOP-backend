package tdop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.TrustScore;
import tdop.entity.User;
import tdop.repository.TrustScoreRepository;
import tdop.repository.UserRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrustServiceTest {

    @Mock
    private TrustScoreRepository trustScoreRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrustService trustService;

    @Test
    void testGetTrustScoreExists() {
        User user = User.builder().id(1L).build();
        TrustScore score = TrustScore.builder().id(1L).user(user).overallScore(85).build();
        when(trustScoreRepository.findByUserId(1L)).thenReturn(Optional.of(score));

        TrustScore result = trustService.getTrustScore(1L);
        assertEquals(85, result.getOverallScore());
    }

    @Test
    void testGetTrustScoreNotFound() {
        when(trustScoreRepository.findByUserId(999L)).thenReturn(Optional.empty());

        TrustScore result = trustService.getTrustScore(999L);
        assertNull(result);
    }
}
