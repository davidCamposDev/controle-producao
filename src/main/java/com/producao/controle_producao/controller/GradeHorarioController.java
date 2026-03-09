package com.producao.controle_producao.controller;

import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade-horario")
public class GradeHorarioController {

    private final GradeHorarioRepository gradeHorarioRepository;

    public GradeHorarioController(GradeHorarioRepository gradeHorarioRepository) {
        this.gradeHorarioRepository = gradeHorarioRepository;
    }

    @GetMapping("/turno/{turnoId}")
    public List<GradeHorario> buscarPorTurno(@PathVariable Long turnoId) {
        return gradeHorarioRepository.findByTurnoIdOrderByHoraInicio(turnoId);
    }
}
