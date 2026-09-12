package tdop.mapper;

import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public static <T, R> T map(R source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Mapping failed", e);
        }
    }
}
