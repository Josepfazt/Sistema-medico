package com.clinica.sistemamedico.dto;

import lombok.Data;

@Data
public class Verify2FARequest {
    private String email;
    private String code;
}