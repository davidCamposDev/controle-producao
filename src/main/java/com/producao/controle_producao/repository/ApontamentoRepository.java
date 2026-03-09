package com.producao.controle_producao.repository;

import com.producao.controle_producao.entity.Apontamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ApontamentoRepository extends JpaRepository<Apontamento, Long> {
    List<Apontamento> findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(
            LocalDate data,
            Long turnoId
    );

}
