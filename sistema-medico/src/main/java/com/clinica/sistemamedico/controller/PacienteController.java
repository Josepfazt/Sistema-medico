package com.clinica.sistemamedico.controller;

import com.clinica.sistemamedico.model.*;
import com.clinica.sistemamedico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/paciente")
public class PacienteController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private CitaRepository citaRepository;

    // Ver perfil del paciente (NO modificable)
    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar mis citas activas (No gestión, no eliminación)
    @GetMapping("/citas/activas/{pacienteId}")
    public List<Cita> listarCitasActivas(@PathVariable Integer pacienteId) {
        List<Cita> todas = citaRepository.findByPacienteId(pacienteId);
        todas.removeIf(c -> c.getEstado() != Cita.EstadoCita.PENDIENTE);
        return todas;
    }

    // Listar mis citas completadas (Historial)
    @GetMapping("/citas/completadas/{pacienteId}")
    public List<Cita> listarCitasCompletadas(@PathVariable Integer pacienteId) {
        List<Cita> todas = citaRepository.findByPacienteId(pacienteId);
        todas.removeIf(c -> c.getEstado() == Cita.EstadoCita.PENDIENTE);
        return todas;
    }

    // Obtener todos los médicos para el Combobox de especialidad/médicos
    @GetMapping("/medicos")
    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    // Obtener las disponibilidades de un médico específico (Tabla informativa)
    @GetMapping("/medico/{medicoId}/disponibilidad")
    public List<Disponibilidad> listarDisponibilidadMedico(@PathVariable Integer medicoId) {
        return disponibilidadRepository.findByMedicoUsuarioIdAndDiaSemanaAndEstado(medicoId, null, Disponibilidad.EstadoDisponibilidad.APROBADO);
    }

    // Agendar una nueva Cita
    @PostMapping("/agendar-cita")
    public ResponseEntity<?> agendarCita(@RequestBody Map<String, Object> payload) {
        try {
            Integer pacienteId = (Integer) payload.get("pacienteId");
            Integer medicoId = (Integer) payload.get("medicoId");
            LocalDate fecha = LocalDate.parse((String) payload.get("fecha"));
            java.time.LocalTime horaInicio = java.time.LocalTime.parse((String) payload.get("horaInicio"));
            java.time.LocalTime horaFin = java.time.LocalTime.parse((String) payload.get("horaFin"));

            Usuario paciente = usuarioRepository.findById(pacienteId).orElseThrow();
            Medico medico = medicoRepository.findById(medicoId).orElseThrow();

            Cita nuevaCita = new Cita();
            nuevaCita.setPaciente(paciente);
            nuevaCita.setMedico(medico);
            nuevaCita.setFecha(fecha);
            nuevaCita.setHoraInicio(horaInicio);
            nuevaCita.setHoraFin(horaFin);
            nuevaCita.setEstado(Cita.EstadoCita.PENDIENTE);

            citaRepository.save(nuevaCita);
            return ResponseEntity.ok(Map.of("message", "Cita agendada correctamente"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al agendar cita"));
        }
    }
}