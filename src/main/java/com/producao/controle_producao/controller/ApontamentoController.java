package com.producao.controle_producao.controller;

import com.producao.controle_producao.entity.Apontamento;
import com.producao.controle_producao.service.ApontamentoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apontamentos")
public class ApontamentoController {
    private final ApontamentoService apontamentoService;

    public ApontamentoController(ApontamentoService apontamentoService) {
        this.apontamentoService = apontamentoService;
    }

    @PostMapping
    public Apontamento criar(@RequestBody Apontamento apontamento) {
        return apontamentoService.salvar(apontamento);
    }

    @GetMapping
    public List<Apontamento> listar() {
        return apontamentoService.listarTodos();
    }

    @GetMapping("/data/{data}/turno/{turnoId}")
    public List<Apontamento> buscarPorDataETurno(
            @PathVariable LocalDate data,
            @PathVariable Long turnoId) {

        return apontamentoService.buscarPorDataETurno(data, turnoId);
    }
    @GetMapping("/resumo/{data}/turno/{turnoId}")
    public Map<String, Object> resumoTurno(
            @PathVariable LocalDate data,
            @PathVariable Long turnoId) {

        return apontamentoService.resumoTurno(data, turnoId);
    }

    @PutMapping("/{id}")
    public Apontamento atualizar(@PathVariable Long id, @RequestBody Apontamento apontamento) {
        apontamento.setId(id);
        return apontamentoService.salvar(apontamento);
    }
}
