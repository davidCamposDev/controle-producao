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
        Long gradeId = apontamento.getGradeHorario().getId();
        // Evitar duplicação
        Optional<Apontamento> existente =
                apontamentoRepository.findByDataAndGradeHorarioId(apontamento.getData(), gradeId);

        if (existente.isPresent() && (apontamento.getId() == null ||
                !existente.get().getId().equals(apontamento.getId()))) {

            throw new RuntimeException("Já existe apontamento para esse horário nesse dia");
        }
        //Verificação de apontamento existente
        if (apontamento.getId() != null) {
            apontamentoRepository.findById(apontamento.getId())
                    .orElseThrow(() -> new RuntimeException("Apontamento não encontrado"));
        }

        if (apontamento.getGradeHorario() == null) {
            throw new RuntimeException("GradeHorario é obrigatório");
        }

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

    //Fazer Consultas no BD Listando todos e por Turno
    public List<Apontamento> listarTodos() {
        return apontamentoRepository.findAll();
    }

    public List<Apontamento> buscarPorDataETurno(LocalDate data, Long turnoId) {
        return apontamentoRepository
                .findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);
    }

    //Metricas da Produção Resumo de turno

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

    //Apresentação do Grafico Hora a Hora turno
    public List<Map<String, Object>> graficoTurno(LocalDate data, Long turnoId) {

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdOrderByHoraInicio(turnoId);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        Map<Long, Apontamento> mapaApontamentos = new HashMap<>();
        for (Apontamento a : apontamentos) {
            mapaApontamentos.put(a.getGradeHorario().getId(), a);
        }

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (GradeHorario g : grade) {

            int minutos = g.getMinutosPlanejados();

            double metaBloco = capacidadePorMinuto * minutos * EFICIENCIA_DESEJADA;

            int producaoReal = 0;

            if (mapaApontamentos.containsKey(g.getId())) {
                producaoReal = mapaApontamentos.get(g.getId()).getQuantidadeProduzida();
            }

            double diferenca = producaoReal - metaBloco;

            Map<String, Object> linha = new HashMap<>();
            linha.put("hora", g.getHoraInicio());
            linha.put("meta", metaBloco);
            linha.put("producao", producaoReal);
            linha.put("diferenca", diferenca);

            resultado.add(linha);
        }

        return resultado;
    }

    //Curva da Produção do Turno
    public List<Map<String, Object>> curvaTurno(LocalDate data, Long turnoId) {

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdOrderByHoraInicio(turnoId);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        Map<Long, Apontamento> mapa = new HashMap<>();
        for (Apontamento a : apontamentos) {
            mapa.put(a.getGradeHorario().getId(), a);
        }

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;

        int producaoAcumulada = 0;
        double metaAcumulada = 0;

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (GradeHorario g : grade) {

            int minutos = g.getMinutosPlanejados();

            double metaBloco = capacidadePorMinuto * minutos * EFICIENCIA_DESEJADA;

            int producaoBloco = 0;
            if (mapa.containsKey(g.getId())) {
                producaoBloco = mapa.get(g.getId()).getQuantidadeProduzida();
            }

            producaoAcumulada += producaoBloco;
            metaAcumulada += metaBloco;

            Map<String, Object> linha = new HashMap<>();
            linha.put("hora", g.getHoraInicio());
            linha.put("metaAcumulada", metaAcumulada);
            linha.put("producaoAcumulada", producaoAcumulada);
            linha.put("diferenca", producaoAcumulada - metaAcumulada);

            resultado.add(linha);
        }

        return resultado;
    }

    //Percentual da meta diaria

    private static final int META_DIARIA = 1908;

    public Map<String, Object> progressoMeta(LocalDate data, Long turnoId) {

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        int producaoAtual = 0;

        for (Apontamento a : apontamentos) {
            producaoAtual += a.getQuantidadeProduzida();
        }

        double percentual = (producaoAtual * 100.0) / META_DIARIA;
        percentual = Math.round(percentual * 100.0) / 100.0;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("producaoAtual", producaoAtual);
        resultado.put("metaDiaria", META_DIARIA);
        resultado.put("percentual", percentual);

        return resultado;
    }

}
