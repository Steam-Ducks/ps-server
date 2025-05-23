package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.controller.PositionController;
import pointsystem.dto.position.PositionDto;
import pointsystem.service.PositionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionControllerTest {

    private PositionService positionService;
    private PositionController positionController;

    @BeforeEach
    void setUp() {
        positionService = mock(PositionService.class);
        positionController = new PositionController(positionService);
    }

    @Test
    void testCreatePosition() {
        PositionDto dto = new PositionDto();
        when(positionService.createPosition(dto)).thenReturn(1);

        ResponseEntity<Integer> response = positionController.createPosition(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/api/positions/1"));
    }

    @Test
    void testGetPositionByIdFound() {
        PositionDto dto = new PositionDto();
        when(positionService.getPositionById(1)).thenReturn(Optional.of(dto));

        ResponseEntity<PositionDto> response = positionController.getPositionById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetPositionByIdNotFound() {
        when(positionService.getPositionById(1)).thenReturn(Optional.empty());

        ResponseEntity<PositionDto> response = positionController.getPositionById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllPositions() {
        List<PositionDto> list = List.of(new PositionDto(), new PositionDto());
        when(positionService.getAllPositions()).thenReturn(list);

        ResponseEntity<List<PositionDto>> response = positionController.getAllPositions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void testUpdatePositionSuccess() {
        PositionDto dto = new PositionDto();

        ResponseEntity<Void> response = positionController.updatePositionById(1, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(positionService).updatePositionById(1, dto);
    }

    @Test
    void testUpdatePositionThrowsGenericException() {
        PositionDto dto = new PositionDto();
        doThrow(new RuntimeException("error")).when(positionService).updatePositionById(eq(1), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            positionController.updatePositionById(1, dto);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void testDeletePositionSuccess() {
        ResponseEntity<Void> response = positionController.deletePositionById(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(positionService).deletePositionById(1);
    }

    @Test
    void testDeletePositionThrowsGenericException() {
        doThrow(new RuntimeException("error")).when(positionService).deletePositionById(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            positionController.deletePositionById(1);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }
}

