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
import java.util.*;

@Service
public class ProducaoAnalyticsService {

    private final ApontamentoRepository apontamentoRepository;
    private final GradeHorarioRepository gradeHorarioRepository;

    public ProducaoAnalyticsService(
            ApontamentoRepository apontamentoRepository,
            GradeHorarioRepository gradeHorarioRepository) {

        this.apontamentoRepository = apontamentoRepository;
        this.gradeHorarioRepository = gradeHorarioRepository;
    }

    // 🔥 MÉTODO AUXILIAR (evita repetir código)
    private String getDiaSemana(LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();

        return switch (dia) {
            case MONDAY -> "SEGUNDA";
            case TUESDAY -> "TERCA";
            case WEDNESDAY -> "QUARTA";
            case THURSDAY -> "QUINTA";
            case FRIDAY -> "SEXTA";
            default -> "SEGUNDA";
        };
    }

    // 📊 GRÁFICO HORA A HORA
    public List<GraficoTurnoDTO> graficoTurno(LocalDate data, Long turnoId) {

        String diaSemana = getDiaSemana(data);

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdAndDiaSemanaOrderByHoraInicio(turnoId, diaSemana);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        Map<Long, Apontamento> mapaApontamentos = new HashMap<>();
        for (Apontamento a : apontamentos) {
            mapaApontamentos.put(a.getGradeHorario().getId(), a);
        }

        List<GraficoTurnoDTO> resultado = new ArrayList<>();

        for (GradeHorario g : grade) {

            double metaBloco = g.getMeta(); // 🔥 AGORA VEM DO BANCO

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

    // 📈 PROGRESSO DO TURNO
    public progressoMetaDTO ProgressoMeta(LocalDate data, Long turnoId) {

        String diaSemana = getDiaSemana(data);

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdAndDiaSemanaOrderByHoraInicio(turnoId, diaSemana);

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        int producaoAtual = 0;

        for (Apontamento a : apontamentos) {
            producaoAtual += a.getQuantidadeProduzida();
        }

        // 🔥 META TOTAL VEM DO BANCO (não calcula mais)
        double metaTurno = grade.stream()
                .mapToDouble(GradeHorario::getMeta)
                .sum();

        double percentual = metaTurno > 0
                ? (producaoAtual / metaTurno) * 100
                : 0;

        return new progressoMetaDTO(producaoAtual, metaTurno, percentual);
    }
}