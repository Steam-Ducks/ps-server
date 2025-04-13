package pointsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointsystem.dto.authentication.AuthenticationRequestDto;
import pointsystem.dto.authentication.AuthenticationResponseDto;
import pointsystem.dto.authentication.RegisterRequestDto;
import pointsystem.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        AuthenticationResponseDto response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto request) {
        AuthenticationResponseDto response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}