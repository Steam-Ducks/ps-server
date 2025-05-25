package pointsystem.integration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.service.SupabaseStorageService;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration") // execute apenas com este profile ativo
@EnabledIfEnvironmentVariable(named = "SUPABASE_INTEGRATION", matches = "true") // execute só com variável de ambiente ativa
public class SupabaseStorageServiceTest {

    @Autowired
    private SupabaseStorageService storageService;

    @Test
    void deveFazerUploadDeImagemValida() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/test-files/test-image.png");
        assertNotNull(inputStream, "Imagem de teste não encontrada");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                inputStream
        );

        String url = storageService.uploadEmployeePhoto(file);
        assertNotNull(url);
        assertTrue(url.startsWith("https://iscjueykmwxxzoanzcoo.supabase.co/storage/v1/object/public/userfiles/photos/"));
    }

    @Test
    void deveAtualizarImagemExistente() throws Exception {
        InputStream streamOld = getClass().getResourceAsStream("/test-files/test-image.png");
        InputStream streamNew = getClass().getResourceAsStream("/test-files/test-image-new.jpg");

        assertNotNull(streamOld);
        assertNotNull(streamNew);

        MockMultipartFile oldFile = new MockMultipartFile("file", "old.png", "image/png", streamOld);
        MockMultipartFile newFile = new MockMultipartFile("file", "new.jpg", "image/jpeg", streamNew);

        String oldUrl = storageService.uploadEmployeePhoto(oldFile);
        assertNotNull(oldUrl);

        String newUrl = storageService.updateEmployeePhoto(oldUrl, newFile);
        assertNotNull(newUrl);
        assertNotEquals(oldUrl, newUrl);
    }
}
