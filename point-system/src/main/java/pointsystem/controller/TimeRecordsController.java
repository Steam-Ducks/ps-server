package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.config.JwtUtil;
import pointsystem.dto.timeRecords.TimeRecordsDto;
import pointsystem.service.TimeRecordsService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/timerecords")
public class TimeRecordsController {

    private final TimeRecordsService timeRecordsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TimeRecordsController(TimeRecordsService timeRecordsService, JwtUtil jwtUtil) {
        this.timeRecordsService = timeRecordsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{timeRecordsId}")
    public ResponseEntity<TimeRecordsDto> getTimeRecordsById(@PathVariable int timeRecordsId) {
        return timeRecordsService.getTimeRecordsById(timeRecordsId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TimeRecordsDto>> getAllTimeRecords() {
        return ResponseEntity.ok(timeRecordsService.getAllTimeRecords());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TimeRecordsDto>> getTimeRecordsByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {

        return ResponseEntity.ok(
                timeRecordsService.getTimeRecordsByEmployeeId(employeeId, startDate, endDate)
        );
    }



    @PostMapping
    public ResponseEntity<TimeRecordsDto> createTimeRecords(@RequestBody TimeRecordsDto timeRecordsDto) {
        try {
            TimeRecordsDto createdRecord = timeRecordsService.createTimeRecords(timeRecordsDto);
            return ResponseEntity.created(URI.create("/api/timerecords/" + createdRecord.getId()))
                    .body(createdRecord);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating time record", e);
        }
    }

    @PutMapping("/{timeRecordsId}")
    public ResponseEntity<Void> updateTimeRecordsById(
            @PathVariable int timeRecordsId,
            @RequestBody TimeRecordsDto timeRecordsDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);

            timeRecordsService.updateTimeRecordsById(timeRecordsId, timeRecordsDto, email);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{timeRecordsId}")
    public ResponseEntity<Void> deleteTimeRecordsById(@PathVariable int timeRecordsId) {
        try {
            timeRecordsService.deleteTimeRecordsById(timeRecordsId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        }
    }
}