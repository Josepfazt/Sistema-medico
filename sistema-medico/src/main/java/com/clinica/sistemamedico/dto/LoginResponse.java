package com.clinica.sistemamedico.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean requires2fa;
    private String qrCodeUrl; // Se enviará solo la primera vez para que lo escaneen
    private String email;
    private String rol;
    private Integer userId;
    private String token; // Se entregará tras pasar el 2FA exitosamente
}