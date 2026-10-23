package com.clinica.sistemamedico.repository;

import com.clinica.sistemamedico.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Usuario.Rol rol);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
}