package com.producao.controle_producao.controller;

import com.producao.controle_producao.dto.GraficoTurnoDTO;
import com.producao.controle_producao.dto.progressoMetaDTO;
import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.service.ApontamentoService;
import com.producao.controle_producao.service.ProducaoAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apontamentos")
public class ApontamentoController {
    private final ApontamentoService apontamentoService;
    private final ProducaoAnalyticsService producaoAnalyticsService;

    public ApontamentoController(
            ApontamentoService apontamentoService,
            ProducaoAnalyticsService producaoAnalyticsService) {

        this.apontamentoService = apontamentoService;
        this.producaoAnalyticsService = producaoAnalyticsService;
    }

    @PostMapping
    public Apontamento criar(@RequestBody Apontamento apontamento) {
        return apontamentoService.salvar(apontamento);
    }
    //Listagem do apontamento
    @GetMapping
    public List<Apontamento> listar() {
        return apontamentoService.listarTodos();
    }
    //Pesquisa por data
    @GetMapping("/data/{data}/turno/{turnoId}")
    public List<Apontamento> buscarPorDataETurno(
            @PathVariable LocalDate data,
            @PathVariable Long turnoId) {

        return apontamentoService.buscarPorDataETurno(data, turnoId);
    }

    @GetMapping("/grafico/{data}/turno/{turnoId}")
    public List<GraficoTurnoDTO> graficoTurno(
            @PathVariable LocalDate data,
            @PathVariable Long turnoId) {

        return producaoAnalyticsService.graficoTurno(data, turnoId);
    }
    //Atualizar Hora a Hora
    @PutMapping("/{id}")
    public Apontamento atualizar(@PathVariable Long id, @RequestBody Apontamento apontamento) {
        apontamento.setId(id);
        return apontamentoService.salvar(apontamento);
    }
    //Percentual da meta diaria
    @GetMapping("/progresso/{data}/turno/{turnoId}")
    public progressoMetaDTO ProgressoMeta(
            @PathVariable LocalDate data,
            @PathVariable Long turnoId) {

        return producaoAnalyticsService.ProgressoMeta(data, turnoId);
    }
}
