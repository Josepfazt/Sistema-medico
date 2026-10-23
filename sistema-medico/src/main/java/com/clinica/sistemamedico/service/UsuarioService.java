package com.clinica.sistemamedico.service;

import com.clinica.sistemamedico.dto.RegistroRequest;
import com.clinica.sistemamedico.model.Usuario;
import com.clinica.sistemamedico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TwoFactorAuthService tfaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarPaciente(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya se encuentra registrado.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        // Cifrado de contraseña estricto
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setNombres(request.getNombres());
        nuevoUsuario.setApellidoPaterno(request.getApellidoPaterno());
        nuevoUsuario.setApellidoMaterno(request.getApellidoMaterno());
        nuevoUsuario.setDni(request.getDni());
        nuevoUsuario.setTelefono(request.getTelefono());
        nuevoUsuario.setRol(Usuario.Rol.PACIENTE);
        
        // Generamos su secreto 2FA único desde el primer instante
        nuevoUsuario.setSecret2fa(tfaService.generarClaveSecreta());
        nuevoUsuario.setIs2faEnabled(false); // Se activará cuando valide su primer QR exitosamente

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Envío de correo asíncrono/independiente
        emailService.enviarCorreoBienvenida(usuarioGuardado.getEmail(), usuarioGuardado.getNombres());

        return usuarioGuardado;
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean verificarPassword(String passwordPlana, String passwordCifrada) {
        return passwordEncoder.matches(passwordPlana, passwordCifrada);
    }
    
    public void activar2FA(Usuario usuario) {
        usuario.setIs2faEnabled(true);
        usuarioRepository.save(usuario);
    }
}