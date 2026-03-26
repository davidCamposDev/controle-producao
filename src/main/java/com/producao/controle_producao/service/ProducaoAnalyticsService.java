package com.producao.controle_producao.service;

import com.producao.controle_producao.dto.GraficoTurnoDTO;
import com.producao.controle_producao.dto.progressoMetaDTO;
import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.repository.ApontamentoRepository;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProducaoAnalyticsService {

    private final ApontamentoRepository apontamentoRepository;
    private final GradeHorarioRepository gradeHorarioRepository;

    private static final double CAPACIDADE_HORA = 273.0;
    private static final double EFICIENCIA_DESEJADA = 0.88;
    private static final int META_DIARIA = 1908;

    public ProducaoAnalyticsService(
            ApontamentoRepository apontamentoRepository,
            GradeHorarioRepository gradeHorarioRepository) {

        this.apontamentoRepository = apontamentoRepository;
        this.gradeHorarioRepository = gradeHorarioRepository;
    }

    //Apresentação do Grafico Hora a Hora turno
    public List<GraficoTurnoDTO> graficoTurno(LocalDate data, Long turnoId) {

        DayOfWeek dia = data.getDayOfWeek();

        String diaSemana = switch (dia) {
            case MONDAY -> "SEGUNDA";
            case TUESDAY -> "TERCA";
            case WEDNESDAY -> "QUARTA";
            case THURSDAY -> "QUINTA";
            case FRIDAY -> "SEXTA";
            default -> "SEGUNDA";
        };

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdAndDiaSemanaOrderByHoraInicio(turnoId, diaSemana);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        Map<Long, Apontamento> mapaApontamentos = new HashMap<>();
        for (Apontamento a : apontamentos) {
            mapaApontamentos.put(a.getGradeHorario().getId(), a);
        }

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;

        List<GraficoTurnoDTO> resultado = new ArrayList<>();

        for (GradeHorario g : grade) {

            int minutos = g.getMinutosPlanejados();

            double metaBloco = capacidadePorMinuto * minutos * EFICIENCIA_DESEJADA;

            int producaoReal = 0;
            String observacao = "";

            if (mapaApontamentos.containsKey(g.getId())) {
                Apontamento a = mapaApontamentos.get(g.getId());
                producaoReal = a.getQuantidadeProduzida();
                observacao = a.getObservacao();
            }

            double diferenca = producaoReal - metaBloco;

            resultado.add(
                    new GraficoTurnoDTO(
                            g.getId(),
                            g.getHoraInicio(),
                            metaBloco,
                            producaoReal,
                            diferenca,
                            observacao
                    )
            );
        }

        return resultado;
    }

    //Percentual da meta diaria
    public progressoMetaDTO ProgressoMeta(LocalDate data, Long turnoId) {

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdOrderByHoraInicio(turnoId);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        int producaoAtual = 0;
        int minutosPlanejados = 0;

        for (GradeHorario g  : grade){
            minutosPlanejados += g.getMinutosPlanejados();
        }

        for (Apontamento a : apontamentos) {
            producaoAtual += a.getQuantidadeProduzida();
        }

        double capacidadePorMinuto = CAPACIDADE_HORA / 60.0;
        double metaTurno = minutosPlanejados * capacidadePorMinuto * EFICIENCIA_DESEJADA;
        double percentual = producaoAtual / metaTurno * 100;


        return new progressoMetaDTO(producaoAtual, metaTurno, percentual);
    }
}
