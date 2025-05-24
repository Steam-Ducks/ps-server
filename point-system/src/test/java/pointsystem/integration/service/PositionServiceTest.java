package pointsystem.integration.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pointsystem.dto.position.PositionDto;
import pointsystem.service.PositionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PositionServiceTest {

    @Autowired
    private PositionService positionService;

    private PositionDto positionDto;

    @BeforeEach
    void setup() {
        positionDto = new PositionDto();
        positionDto.setName("Desenvolvedor Backend");
    }

    @Test
    void deveCriarNovaPosition() {
        Integer id = positionService.createPosition(positionDto);
        assertNotNull(id);
    }

    @Test
    void deveBuscarPositionPorId() {
        Integer id = positionService.createPosition(positionDto);
        Optional<PositionDto> buscado = positionService.getPositionById(id);

        assertTrue(buscado.isPresent());
        assertEquals(positionDto.getName(), buscado.get().getName());
    }

    @Test
    void deveListarTodasAsPositions() {
        positionService.createPosition(positionDto);
        List<PositionDto> positions = positionService.getAllPositions();

        assertFalse(positions.isEmpty());
    }

    @Test
    void deveAtualizarPosition() {
        Integer id = positionService.createPosition(positionDto);
        PositionDto novoDto = new PositionDto();
        novoDto.setName("Analista de Dados");

        positionService.updatePositionById(id, novoDto);
        Optional<PositionDto> atualizada = positionService.getPositionById(id);

        assertTrue(atualizada.isPresent());
        assertEquals("Analista de Dados", atualizada.get().getName());
    }

    @Test
    void deveDeletarPosition() {
        Integer id = positionService.createPosition(positionDto);
        positionService.deletePositionById(id);

        Optional<PositionDto> excluida = positionService.getPositionById(id);
        assertTrue(excluida.isEmpty());
    }

    @Test
    void deveLancarExcecaoAoAtualizarInexistente() {
        PositionDto novoDto = new PositionDto();
        novoDto.setName("Gerente");

        var ex = assertThrows(RuntimeException.class, () -> {
            positionService.updatePositionById(9999, novoDto);
        });

        assertTrue(ex.getMessage().contains("Position not found"));
    }

    @Test
    void deveLancarExcecaoAoDeletarInexistente() {
        var ex = assertThrows(RuntimeException.class, () -> {
            positionService.deletePositionById(9999);
        });

        assertTrue(ex.getMessage().contains("Position not found"));
    }
}

