package com.clinica.sistemamedico.controller;

import com.clinica.sistemamedico.dto.*;
import com.clinica.sistemamedico.model.Usuario;
import com.clinica.sistemamedico.service.UsuarioService;
import com.clinica.sistemamedico.service.TwoFactorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TwoFactorAuthService tfaService;

    // 1. REGISTRO DE PACIENTES
    @PostMapping("/registro")
    public ResponseEntity<?> registrarPaciente(@RequestBody RegistroRequest request) {
        try {
            Usuario usuario = usuarioService.registrarPaciente(request);
            return ResponseEntity.ok(Map.of("message", "Registro exitoso", "email", usuario.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. LOGIN UNIFICADO (Fase 1: Validar credenciales y comprobar 2FA)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(request.getEmail());

        if (usuarioOpt.isEmpty() || !usuarioService.verificarPassword(request.getPassword(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Credenciales incorrectas"));
        }

        Usuario usuario = usuarioOpt.get();
        LoginResponse response = new LoginResponse();
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol().name());
        response.setUserId(usuario.getId());

        // Si es la primera vez que se logea, le mandamos el QR para escanear
        if (!usuario.getIs2faEnabled()) {
            response.setRequires2fa(true);
            String qrUrl = tfaService.obtenerQrCodeUrl(usuario.getSecret2fa(), usuario.getEmail());
            response.setQrCodeUrl(qrUrl);
        } else {
            // Ya tiene el 2FA activo, solo requiere que ponga el código de su app
            response.setRequires2fa(true);
            response.setQrCodeUrl(null);
        }

        return ResponseEntity.ok(response);
    }

    // 3. VERIFICACIÓN DEL 2FA (Fase 2: Validar el Token de Google Authenticator)
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody Verify2FARequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(request.getEmail());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        int codeInt;
        try {
            codeInt = Integer.parseInt(request.getCode());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "El código debe ser numérico"));
        }

        boolean isValid = tfaService.verificarCodigo(usuario.getSecret2fa(), codeInt);

        if (isValid) {
            if (!usuario.getIs2faEnabled()) {
                usuarioService.activar2FA(usuario); // Lo marcamos como activo permanentemente
            }
            // Retornamos el éxito del Login Completo
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "userId", usuario.getId(),
                "rol", usuario.getRol().name(),
                "email", usuario.getEmail()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Código 2FA inválido. Reintente."));
        }
    }
}