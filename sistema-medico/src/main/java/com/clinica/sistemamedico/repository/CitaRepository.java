package com.clinica.sistemamedico.repository;

import com.clinica.sistemamedico.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByPacienteId(Integer pacienteId);
    List<Cita> findByMedicoUsuarioId(Integer medicoId);
    List<Cita> findByMedicoUsuarioIdAndFechaAndEstado(Integer medicoId, LocalDate fecha, Cita.EstadoCita estado);
}