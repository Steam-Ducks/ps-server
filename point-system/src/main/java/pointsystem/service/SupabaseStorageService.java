package pointsystem.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    private static final String SUPABASE_URL = "https://iscjueykmwxxzoanzcoo.supabase.co/storage/v1/object/userfiles/photos/";
    private static final String SUPABASE_PUBLIC_URL = "https://iscjueykmwxxzoanzcoo.supabase.co/storage/v1/object/public/userfiles/photos/";

    @Value("${supabase.auth.token}")
    private String supabaseAuthToken; ;

    public String uploadEmployeePhoto(MultipartFile file) {
        try {
            String contentType = file.getContentType();

            if (!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
                throw new IllegalArgumentException("Tipo de arquivo não suportado. Apenas PNG e JPEG são permitidos.");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + uniqueFileName))
                    .header("Authorization", supabaseAuthToken)
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return SUPABASE_PUBLIC_URL + uniqueFileName;
            } else {
                throw new RuntimeException("Erro ao enviar imagem para Supabase: " + response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro no upload da imagem: " + e.getMessage(), e);
        }
    }

}
