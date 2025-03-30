package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.position.CreatePositionDto;
import pointsystem.dto.position.UpdatePositionDto;
import pointsystem.entity.Company;
import pointsystem.entity.Position;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public PositionService(PositionRepository positionRepository, CompanyRepository companyRepository) {
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
    }

    public Integer createPosition(CreatePositionDto createPositionDto) {
        Position position = new Position(0, createPositionDto.name());

        Position savedPosition = positionRepository.save(position);
        return savedPosition.getId();
    }

    public Optional<Position> getPositionById(Integer positionId) {
        return positionRepository.findById(positionId);
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public void updatePositionById(Integer positionId, UpdatePositionDto updatePositionDto) {
        Optional<Position> positionEntity = positionRepository.findById(positionId);

        if (positionEntity.isPresent()) {
            Position position = positionEntity.get();
            if(updatePositionDto.name() != null) {
                position.setName(updatePositionDto.name());
            }

            positionRepository.save(position);
        }
    }

    public void deletePositionById(Integer positionId) {
        boolean exists = positionRepository.existsById(positionId);
        if (exists) {
            positionRepository.deleteById(positionId);
        }
    }
}
