package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.position.CreatePositionDto;
import pointsystem.dto.position.UpdatePositionDto;
import pointsystem.entity.Position;
import pointsystem.service.PositionService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public ResponseEntity<Integer> createPosition(@RequestBody CreatePositionDto createPositionDto)
    {
        int positionId = positionService.createPosition(createPositionDto);
        return ResponseEntity.created(URI.create("/api/company/" + positionId)).build();
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<Position> getPositionById(@PathVariable int positionId) {
        try {
            Optional<Position> position = positionService.getPositionById(positionId);
            if (position.isPresent()) {
                return ResponseEntity.ok(position.get());
            }
            else
            {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Position>> getAllPositions() {
        try {
            List<Position> positions = positionService.getAllPositions();
            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<Position> updatePositionById(@PathVariable int positionId, @RequestBody UpdatePositionDto positionDto) {
        try {
            positionService.updatePositionById(positionId, positionDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Position> deletePositionById(@PathVariable int positionId) {
        try {
            positionService.deletePositionById(positionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
