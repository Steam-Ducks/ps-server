package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.position.PositionDto;
import pointsystem.service.PositionService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public ResponseEntity<Integer> createPosition(@RequestBody PositionDto positionDto) {
        int positionId = positionService.createPosition(positionDto);
        return ResponseEntity.created(URI.create("/api/positions/" + positionId)).build();
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable int positionId) {
        return positionService.getPositionById(positionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        return ResponseEntity.ok(positionService.getAllPositions());
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<Void> updatePositionById(@PathVariable int positionId, @RequestBody PositionDto positionDto) {
        try {
            positionService.updatePositionById(positionId, positionDto);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Void> deletePositionById(@PathVariable int positionId) {
        try {
            positionService.deletePositionById(positionId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}