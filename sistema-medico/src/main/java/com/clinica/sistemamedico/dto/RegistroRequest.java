package com.clinica.sistemamedico.dto;

import lombok.Data;

@Data
public class RegistroRequest {
    private String email;
    private String password;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String dni;
    private String telefono;
}