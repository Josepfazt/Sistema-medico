package com.clinica.sistemamedico.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoMapper;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(unique = true, nullable = false, length = 15)
    private String dni;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "secret_2fa", length = 255)
    private String secret2fa;

    @Column(name = "is_2fa_enabled")
    private Boolean is2faEnabled = false;

    public enum Rol {
        ADMIN, MEDICO, PACIENTE
    }
}