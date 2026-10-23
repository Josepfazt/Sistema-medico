package com.clinica.sistemamedico.controller;

import com.clinica.sistemamedico.model.*;
import com.clinica.sistemamedico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private CitaRepository citaRepository;
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 1. GESTIÓN DE MÉDICOS (CRUD)
    @GetMapping("/medicos")
    public List<Medico> listarMedicos() { return medicoRepository.findAll(); }

    @PostMapping("/medico")
    public ResponseEntity<?> agregarMedico(@RequestBody Map<String, String> p) {
        try {
            Usuario u = new Usuario();
            u.setEmail(p.get("email"));
            u.setPassword(encoder.encode(p.get("password")));
            u.setNombres(p.get("nombres"));
            u.setApellidoPaterno(p.get("apellidoPaterno"));
            u.setApellidoMaterno(p.get("apellidoMaterno"));
            u.setDni(p.get("dni"));
            u.setTelefono(p.get("telefono"));
            u.setRol(Usuario.Rol.MEDICO);
            u.setSecret2fa(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()).substring(0,10));
            usuarioRepository.save(u);

            Medico m = new Medico();
            m.setUsuario(u);
            m.setSpecialidad(p.get("especialidad"));
            m.setFotoUrl(p.get("fotoUrl")); // Puede ir vacío o null
            medicoRepository.save(m);

            return ResponseEntity.ok(Map.of("message", "Médico agregado con éxito"));
        } catch(Exception e) { return ResponseEntity.badRequest().body(Map.of("error", "Error al crear médico")); }
    }

    @DeleteMapping("/medico/{id}")
    public ResponseEntity<?> eliminarMedico(@PathVariable Integer id) {
        medicoRepository.deleteById(id);
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Médico eliminado"));
    }

    // 2. GESTIÓN DE PACIENTES (Modificar/Eliminar)
    @GetMapping("/pacientes")
    public List<Usuario> listarPacientes() { return usuarioRepository.findByRol(Usuario.Rol.PACIENTE); }

    @PutMapping("/paciente/{id}")
    public ResponseEntity<?> editarPaciente(@PathVariable Integer id, @RequestBody Usuario datos) {
        return usuarioRepository.findById(id).map(p -> {
            p.setNombres(datos.getNombres());
            p.setApellidoPaterno(datos.getApellidoPaterno());
            p.setTelefono(datos.getTelefono());
            usuarioRepository.save(p);
            return ResponseEntity.ok(Map.of("message", "Paciente actualizado en BDD"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/paciente/{id}")
    public ResponseEntity<?> eliminarPaciente(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Paciente eliminado"));
    }

    // 3. MONITOREO DE ADMINISTRADORES (Real-time de seguridad)
    @GetMapping("/administradores")
    public List<Usuario> listarAdmins() { return usuarioRepository.findByRol(Usuario.Rol.ADMIN); }

    // Modificar sus propios datos de Admin logeado en Real-time desde Configuración
    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> editarPropioPerfil(@PathVariable Integer id, @RequestBody Map<String, String> p) {
        return usuarioRepository.findById(id).map(admin -> {
            admin.setNombres(p.get("nombres"));
            admin.setEmail(p.get("email"));
            if (p.get("password") != null && !p.get("password").isEmpty()) {
                admin.setPassword(encoder.encode(p.get("password")));
            }
            usuarioRepository.save(admin);
            return ResponseEntity.ok(Map.of("message", "Tus datos se actualizaron en tiempo real"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. APROBAR O ELIMINAR HORARIOS
    @GetMapping("/horarios/buscar")
    public List<Disponibilidad> buscarHorarios() {
        return disponibilidadRepository.findAll();
    }

    @PutMapping("/horario/{id}/aprobar")
    public ResponseEntity<?> aprobarHorario(@PathVariable Integer id) {
        return disponibilidadRepository.findById(id).map(d -> {
            d.setEstado(Disponibilidad.EstadoDisponibilidad.APROBADO);
            disponibilidadRepository.save(d);
            return ResponseEntity.ok(Map.of("message", "Horario Aprobado. Ya visible para pacientes."));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/horario/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable Integer id) {
        disponibilidadRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Horario rechazado/eliminado de forma permanente"));
    }

    // 5. DATA ANALYTICS (Para los gráficos de barra del Dashboard)
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        long totalPacientes = usuarioRepository.findByRol(Usuario.Rol.PACIENTE).size();
        long totalMedicos = medicoRepository.count();
        long totalCitas = citaRepository.count();

        // Armamos una estructura simple de datos útil para renderizar gráficos de barras
        List<Map<String, Object>> citasPorMedico = new ArrayList<>();
        medicoRepository.findAll().forEach(m -> {
            long atendidas = citaRepository.findByMedicoUsuarioId(m.getUsuarioId())
                    .stream().filter(c -> c.getEstado() == Cita.EstadoCita.REALIZADO).count();
            citasPorMedico.add(Map.of("medico", m.getUsuario().getNombres() + " " + m.getUsuario().getApellidoPaterno(), "atendidas", atendidas));
        });

        return ResponseEntity.ok(Map.of(
            "pacientes", totalPacientes,
            "medicos", totalMedicos,
            "citasTotales", totalCitas,
            "graficoBarras", citasPorMedico
        ));
    }
}