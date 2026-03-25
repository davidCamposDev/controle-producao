package com.producao.controle_producao.dto;

import java.time.LocalTime;

public class GraficoTurnoDTO {
    private LocalTime hora;
    private Long id;
    private double meta;
    private int producao;
    private double diferenca;
    private String observacao;

    public GraficoTurnoDTO(Long id, LocalTime hora, double meta, int producao, double diferenca,String observacao) {
        this.id = id;
        this.hora = hora;
        this.meta = meta;
        this.producao = producao;
        this.diferenca = diferenca;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public LocalTime getHora() {
        return hora;
    }

    public double getMeta() {
        return meta;
    }

    public int getProducao() {
        return producao;
    }

    public double getDiferenca() {
        return diferenca;
    }

    public String getObservacao() {return observacao;}
}
