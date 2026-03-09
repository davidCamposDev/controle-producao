package com.producao.controle_producao.service;

import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.repository.ApontamentoRepository;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Apontamento salvar(Apontamento apontamento) {
        if (apontamento.getId() != null) {
            apontamentoRepository.findById(apontamento.getId())
                    .orElseThrow(() -> new RuntimeException("Apontamento não encontrado"));
        }

        if (apontamento.getGradeHorario() == null) {
            throw new RuntimeException("GradeHorario é obrigatório");
        }

        Long gradeId = apontamento.getGradeHorario().getId();

        GradeHorario bloco = gradeHorarioRepository
                .findById(gradeId)
                .orElseThrow(() -> new RuntimeException("GradeHorario não encontrado"));

        if (apontamento.getQuantidadeProduzida() == null) {
            throw new RuntimeException("Quantidade produzida é obrigatória");
        }

        apontamento.setGradeHorario(bloco);

        int minutos = bloco.getMinutosPlanejados();

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;
        double capacidadeBloco = capacidadePorMinuto * minutos;
        double percentual = apontamento.getQuantidadeProduzida() / capacidadeBloco;

        apontamento.setPercentual(percentual);

        return apontamentoRepository.save(apontamento);
    }

    public List<Apontamento> listarTodos() {
        return apontamentoRepository.findAll();
    }

    public List<Apontamento> buscarPorDataETurno(LocalDate data, Long turnoId) {
        return apontamentoRepository
                .findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);
    }

    public Map<String, Object> resumoTurno(LocalDate data, Long turnoId) {

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        int producaoTotal = 0;
        int minutosPlanejados = 0;

        for (Apontamento a : apontamentos) {
            producaoTotal += a.getQuantidadeProduzida();
            minutosPlanejados += a.getGradeHorario().getMinutosPlanejados();
        }

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;
        double metaEsperada = capacidadePorMinuto * minutosPlanejados * EFICIENCIA_DESEJADA;

        double diferenca = producaoTotal - metaEsperada;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("producaoAtual", producaoTotal);
        resultado.put("metaEsperada", metaEsperada);
        resultado.put("diferenca", diferenca);

        return resultado;
    }

}
