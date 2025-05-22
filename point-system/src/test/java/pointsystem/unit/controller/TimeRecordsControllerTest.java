package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.controller.TimeRecordsController;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.service.TimeRecordsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeRecordsControllerTest {

    @Mock
    private TimeRecordsService timeRecordsService;

    @InjectMocks
    private TimeRecordsController timeRecordsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTimeRecordsById_ReturnsRecord_WhenExists() {
        TimeRecordsDto dto = new TimeRecordsDto();
        when(timeRecordsService.getTimeRecordsById(1)).thenReturn(Optional.of(dto));

        ResponseEntity<TimeRecordsDto> response = timeRecordsController.getTimeRecordsById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getTimeRecordsById_ReturnsNotFound_WhenNotExists() {
        when(timeRecordsService.getTimeRecordsById(1)).thenReturn(Optional.empty());

        ResponseEntity<TimeRecordsDto> response = timeRecordsController.getTimeRecordsById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllTimeRecords_ReturnsList() {
        TimeRecordsDto dto = new TimeRecordsDto();
        when(timeRecordsService.getAllTimeRecords()).thenReturn(Collections.singletonList(dto));

        ResponseEntity<List<TimeRecordsDto>> response = timeRecordsController.getAllTimeRecords();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getTimeRecordsByEmployeeId_ReturnsFilteredList() {
        TimeRecordsDto dto = new TimeRecordsDto();
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(timeRecordsService.getTimeRecordsByEmployeeId(10L, start, end))
                .thenReturn(Arrays.asList(dto));

        ResponseEntity<List<TimeRecordsDto>> response =
                timeRecordsController.getTimeRecordsByEmployeeId(10L, start, end);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createTimeRecords_ReturnsCreatedStatus() {
        TimeRecordsDto dto = new TimeRecordsDto();
        dto.setId(1);
        when(timeRecordsService.createTimeRecords(dto)).thenReturn(dto);

        ResponseEntity<TimeRecordsDto> response = timeRecordsController.createTimeRecords(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void createTimeRecords_ThrowsException() {
        TimeRecordsDto dto = new TimeRecordsDto();
        when(timeRecordsService.createTimeRecords(dto)).thenThrow(new RuntimeException("Error creating time record"));

        assertThrows(ResponseStatusException.class, () -> {
            timeRecordsController.createTimeRecords(dto);
        });
    }

    @Test
    void updateTimeRecordsById_ReturnsNoContent() {
        TimeRecordsDto dto = new TimeRecordsDto();
        doNothing().when(timeRecordsService).updateTimeRecordsById(1, dto);

        ResponseEntity<Void> response = timeRecordsController.updateTimeRecordsById(1, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateTimeRecordsById_ThrowsInternalError() {
        TimeRecordsDto dto = new TimeRecordsDto();
        doThrow(new RuntimeException("Error")).when(timeRecordsService).updateTimeRecordsById(1, dto);

        assertThrows(ResponseStatusException.class, () -> {
            timeRecordsController.updateTimeRecordsById(1, dto);
        });
    }

    @Test
    void deleteTimeRecordsById_ReturnsNoContent() {
        doNothing().when(timeRecordsService).deleteTimeRecordsById(1);

        ResponseEntity<Void> response = timeRecordsController.deleteTimeRecordsById(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteTimeRecordsById_ThrowsNotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(timeRecordsService).deleteTimeRecordsById(1);

        assertThrows(ResponseStatusException.class, () -> {
            timeRecordsController.deleteTimeRecordsById(1);
        });
    }
}
