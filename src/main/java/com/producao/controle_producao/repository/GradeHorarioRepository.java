package com.producao.controle_producao.repository;

import com.producao.controle_producao.entity.GradeHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeHorarioRepository extends JpaRepository<GradeHorario, Long> {
    List<GradeHorario> findByTurnoIdOrderByHoraInicio(Long turnoId);

    List<GradeHorario> findByTurnoIdAndDiaSemanaOrderByHoraInicio(Long turnoId, String diaSemana);
}
