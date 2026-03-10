package com.producao.controle_producao.dto;

import java.time.LocalTime;

public class GraficoTurnoDTO {
    private LocalTime hora;
    private double meta;
    private int producao;
    private double diferenca;

    public GraficoTurnoDTO(LocalTime hora, double meta, int producao, double diferenca) {
        this.hora = hora;
        this.meta = meta;
        this.producao = producao;
        this.diferenca = diferenca;
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
}
