package pointsystem.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.Position;
import pointsystem.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")

public class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    @Test
    public void shouldSaveAndFindPositionById() {
        Position position = new Position();
        position.setName("Backend Developer");
        positionRepository.save(position);

        Optional<Position> found = positionRepository.findById(position.getId());
        assertTrue(found.isPresent());
        assertEquals("Backend Developer", found.get().getName());
    }

    @Test
    public void shouldListAllPositions() {
        Position p1 = new Position();
        p1.setName("DevOps Engineer");

        Position p2 = new Position();
        p2.setName("Product Owner");

        positionRepository.saveAll(List.of(p1, p2));

        List<Position> allPositions = positionRepository.findAll();
        assertEquals(2, allPositions.size());
    }
}

