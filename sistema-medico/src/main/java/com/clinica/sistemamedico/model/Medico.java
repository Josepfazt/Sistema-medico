package com.clinica.sistemamedico.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicos")
@Data
public class Medico {
    @Id
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String specialidad;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;
}