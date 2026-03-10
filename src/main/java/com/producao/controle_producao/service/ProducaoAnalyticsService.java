package com.producao.controle_producao.service;

import com.producao.controle_producao.dto.CurvaTurnoDTO;
import com.producao.controle_producao.dto.GraficoTurnoDTO;
import com.producao.controle_producao.dto.progressoMetaDTO;
import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.repository.ApontamentoRepository;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import org.springframework.stereotype.Service;

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
    public List<GraficoTurnoDTO> graficoTurno(LocalDate data, Long turnoId) {

        List<GradeHorario> grade =
                gradeHorarioRepository.findByTurnoIdOrderByHoraInicio(turnoId);

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

            if (mapaApontamentos.containsKey(g.getId())) {
                producaoReal = mapaApontamentos.get(g.getId()).getQuantidadeProduzida();
            }

            double diferenca = producaoReal - metaBloco;

            resultado.add(
                    new GraficoTurnoDTO(
                            g.getHoraInicio(),
                            metaBloco,
                            producaoReal,
                            diferenca
                    )
            );
        }

        return resultado;
    }

    //Curva da Produção do Turno
    public List<CurvaTurnoDTO> curvaTurno(LocalDate data, Long turnoId) {

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

        List<CurvaTurnoDTO> resultado = new ArrayList<>();

        for (GradeHorario g : grade) {

            int minutos = g.getMinutosPlanejados();

            double metaBloco = capacidadePorMinuto * minutos * EFICIENCIA_DESEJADA;

            int producaoBloco = 0;

            if (mapa.containsKey(g.getId())) {
                producaoBloco = mapa.get(g.getId()).getQuantidadeProduzida();
            }

            producaoAcumulada += producaoBloco;
            metaAcumulada += metaBloco;

            resultado.add(
                    new CurvaTurnoDTO(
                            g.getHoraInicio(),
                            metaAcumulada,
                            producaoAcumulada,
                            producaoAcumulada - metaAcumulada
                    )
            );
        }

        return resultado;
    }

    //Percentual da meta diaria
    public progressoMetaDTO ProgressoMeta(LocalDate data, Long turnoId) {

        List<Apontamento> apontamentos =
                apontamentoRepository.findByDataAndTurnoIdOrderByGradeHorarioHoraInicio(data, turnoId);

        int producaoAtual = 0;

        for (Apontamento a : apontamentos) {
            producaoAtual += a.getQuantidadeProduzida();
        }

        double percentual = (producaoAtual * 100.0) / META_DIARIA;
        percentual = Math.round(percentual * 100.0) / 100.0;

        return new progressoMetaDTO(producaoAtual, META_DIARIA, percentual);
    }
}
