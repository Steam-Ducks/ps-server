package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.PositionConverter;
import pointsystem.dto.position.PositionDto;
import pointsystem.entity.Position;
import pointsystem.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final PositionConverter positionConverter;

    @Autowired
    public PositionService(PositionRepository positionRepository, PositionConverter positionConverter) {
        this.positionRepository = positionRepository;
        this.positionConverter = positionConverter;
    }

    public Integer createPosition(PositionDto positionDto) {
        Position position = positionConverter.toEntity(positionDto);
        Position savedPosition = positionRepository.save(position);
        return savedPosition.getId();
    }

    public Optional<PositionDto> getPositionById(Integer positionId) {
        return positionRepository.findById(positionId)
                .map(positionConverter::toDto);
    }

    public List<PositionDto> getAllPositions() {
        return positionConverter.toDto(positionRepository.findAll());
    }

    public void updatePositionById(Integer positionId, PositionDto positionDto) {
        Optional<Position> positionEntity = positionRepository.findById(positionId);

        if (positionEntity.isPresent()) {
            Position position = positionEntity.get();
            if (positionDto.getName() != null) {
                position.setName(positionDto.getName());
            }
            positionRepository.save(position);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found");
        }
    }

    public void deletePositionById(Integer positionId) {
        if (positionRepository.existsById(positionId)) {
            positionRepository.deleteById(positionId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found");
        }
    }
}