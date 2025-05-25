package pointsystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.PositionConverter;
import pointsystem.dto.position.PositionDto;
import pointsystem.entity.Position;
import pointsystem.repository.PositionRepository;
import pointsystem.service.PositionService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionServiceTest {

    @Mock private PositionRepository positionRepository;
    @Mock private PositionConverter positionConverter;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPosition_ReturnsId_WhenSuccessful() {

        PositionDto dto = PositionDto.builder().name("Analista").build();
        Position position = new Position();
        Position saved = new Position();
        saved.setId(100);

        when(positionConverter.toEntity(dto)).thenReturn(position);
        when(positionRepository.save(position)).thenReturn(saved);


        Integer id = positionService.createPosition(dto);


        assertEquals(100, id);
    }

    @Test
    void getPositionById_ReturnsDto_WhenFound() {

        Position position = new Position();
        PositionDto dto = new PositionDto();
        when(positionRepository.findById(1)).thenReturn(Optional.of(position));
        when(positionConverter.toDto(position)).thenReturn(dto);


        Optional<PositionDto> result = positionService.getPositionById(1);


        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getPositionById_ReturnsEmpty_WhenNotFound() {

        when(positionRepository.findById(999)).thenReturn(Optional.empty());


        Optional<PositionDto> result = positionService.getPositionById(999);


        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPositions_ReturnsListOfDtos() {

        List<Position> positions = List.of(new Position(), new Position());
        List<PositionDto> dtos = List.of(new PositionDto(), new PositionDto());

        when(positionRepository.findAll()).thenReturn(positions);
        when(positionConverter.toDto(positions)).thenReturn(dtos);


        List<PositionDto> result = positionService.getAllPositions();


        assertEquals(dtos, result);
    }

    @Test
    void updatePositionById_UpdatesName_WhenFound() {

        Position position = new Position();
        PositionDto dto = PositionDto.builder().name("Novo Cargo").build();

        when(positionRepository.findById(1)).thenReturn(Optional.of(position));


        positionService.updatePositionById(1, dto);


        assertEquals("Novo Cargo", position.getName());
        verify(positionRepository).save(position);
    }

    @Test
    void updatePositionById_ThrowsException_WhenNotFound() {

        PositionDto dto = PositionDto.builder().name("Novo Nome").build();
        when(positionRepository.findById(999)).thenReturn(Optional.empty());


        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> positionService.updatePositionById(999, dto));

        assertEquals("404 NOT_FOUND \"Position not found\"", ex.getMessage());
    }

    @Test
    void deletePositionById_Deletes_WhenExists() {

        when(positionRepository.existsById(10)).thenReturn(true);


        positionService.deletePositionById(10);


        verify(positionRepository).deleteById(10);
    }

    @Test
    void deletePositionById_ThrowsException_WhenNotExists() {

        when(positionRepository.existsById(11)).thenReturn(false);


        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> positionService.deletePositionById(11));

        assertEquals("404 NOT_FOUND \"Position not found\"", ex.getMessage());
    }
}