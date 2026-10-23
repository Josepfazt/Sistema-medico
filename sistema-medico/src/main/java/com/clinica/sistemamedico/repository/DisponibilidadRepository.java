package com.clinica.sistemamedico.repository;

import com.clinica.sistemamedico.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {
    List<Disponibilidad> findByMedicoUsuarioId(Integer medicoId);
    List<Disponibilidad> findByMedicoUsuarioIdAndDiaSemanaAndEstado(Integer medicoId, String diaSemana, Disponibilidad.EstadoDisponibilidad estado);
    List<Disponibilidad> findByEstado(Disponibilidad.EstadoDisponibilidad estado);
}