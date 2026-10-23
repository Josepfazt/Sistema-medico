package com.clinica.sistemamedico.repository;

import com.clinica.sistemamedico.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    List<Medico> findBySpecialidad(String especialidad);
}