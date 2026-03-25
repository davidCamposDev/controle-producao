package com.producao.controle_producao.service;

import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.repository.ApontamentoRepository;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ApontamentoService {

    private final ApontamentoRepository apontamentoRepository;
    private final GradeHorarioRepository gradeHorarioRepository;

    private static final double CAPACIDADE_HORA = 273.0;
    private static final double EFICIENCIA_DESEJADA = 0.88;

    public ApontamentoService(ApontamentoRepository apontamentoRepository,  GradeHorarioRepository gradeHorarioRepository) {
        this.apontamentoRepository = apontamentoRepository;
        this.gradeHorarioRepository = gradeHorarioRepository;
    }

//Apontamento Salvar os Dados Inseridos

    public Apontamento salvar(Apontamento apontamento) {

        if (apontamento.getGradeHorario() == null) {
            throw new RuntimeException("GradeHorario é obrigatório");
        }

        Long gradeId = apontamento.getGradeHorario().getId();

        Optional<Apontamento> existente =
                apontamentoRepository.findByDataAndGradeHorarioId(apontamento.getData(), gradeId);

        GradeHorario bloco = gradeHorarioRepository
                .findById(gradeId)
                .orElseThrow(() -> new RuntimeException("GradeHorario não encontrado"));

        if (apontamento.getQuantidadeProduzida() == null) {
            throw new RuntimeException("Quantidade produzida é obrigatória");
        }

        int minutos = bloco.getMinutosPlanejados();

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;
        double capacidadeBloco = capacidadePorMinuto * minutos;
        double percentual = apontamento.getQuantidadeProduzida() / capacidadeBloco;

        if (existente.isPresent()) {

            Apontamento a = existente.get();
            a.setQuantidadeProduzida(apontamento.getQuantidadeProduzida());
            a.setObservacao(apontamento.getObservacao());
            a.setPercentual(percentual);

            return apontamentoRepository.save(a);

        }

        apontamento.setGradeHorario(bloco);
        apontamento.setPercentual(percentual);

        return apontamentoRepository.save(apontamento);
    }

    //Fazer Consultas no BD Listando todos e por Turno
    public List<Apontamento> listarTodos() {
        return apontamentoRepository.findAll();
    }

    public List<Apontamento> buscarPorDataETurno(LocalDate data, Long turnoId) {
        return apontamentoRepository
                .findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);
    }
}
