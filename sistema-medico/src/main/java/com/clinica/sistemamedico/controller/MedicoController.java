package com.clinica.sistemamedico.controller;

import com.clinica.sistemamedico.model.*;
import com.clinica.sistemamedico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medico")
public class MedicoController {

    @Autowired private MedicoRepository medicoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private CitaRepository citaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // Ver perfil del médico (NO modificable)
    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar horario de disponibilidad (Queda en PENDIENTE por defecto)
    @PostMapping("/disponibilidad")
    public ResponseEntity<?> registrarDisponibilidad(@RequestBody Map<String, Object> payload) {
        try {
            Integer medicoId = (Integer) payload.get("medicoId");
            String diaSemana = (String) payload.get("diaSemana");
            java.time.LocalTime inicio = java.time.LocalTime.parse((String) payload.get("horaInicio"));
            java.time.LocalTime fin = java.time.LocalTime.parse((String) payload.get("horaFin"));

            Medico medico = medicoRepository.findById(medicoId).orElseThrow();

            Disponibilidad disp = new Disponibilidad();
            disp.setMedico(medico);
            disp.setDiaSemana(diaSemana);
            disp.setHoraInicio(inicio);
            disp.setHoraFin(fin);
            disp.setEstado(Disponibilidad.EstadoDisponibilidad.PENDIENTE);

            disponibilidadRepository.save(disp);
            return ResponseEntity.ok(Map.of("message", "Horario guardado. Esperando aprobación del Administrador."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos incorrectos"));
        }
    }

    // Listar todos los horarios del médico logeado
    @GetMapping("/disponibilidades/{medicoId}")
    public List<Disponibilidad> listarMisDisponibilidades(@PathVariable Integer medicoId) {
        return disponibilidadRepository.findByMedicoUsuarioId(medicoId);
    }

    // Listar las citas del médico (Gestión de citas)
    @GetMapping("/citas/{medicoId}")
    public List<Cita> listarMisCitas(@PathVariable Integer medicoId) {
        return citaRepository.findByMedicoUsuarioId(medicoId);
    }

    // Modificar estado de la cita en tiempo real (REALIZADO / NO_REALIZADO)
    @PutMapping("/cita/{citaId}/estado")
    public ResponseEntity<?> cambiarEstadoCita(@PathVariable Integer citaId, @RequestParam String estado) {
        return citaRepository.findById(citaId).map(cita -> {
            cita.setEstado(Cita.EstadoCita.valueOf(estado.toUpperCase()));
            citaRepository.save(cita);
            return ResponseEntity.ok(Map.of("message", "Estado de cita actualizado en Real-time"));
        }).orElse(ResponseEntity.notFound().build());
    }
}