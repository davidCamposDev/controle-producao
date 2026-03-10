package com.producao.controle_producao.dto;

import java.time.LocalTime;

public class CurvaTurnoDTO {
    private LocalTime hora;
    private double metaAcumulada;
    private int producaoAcumulada;
    private double diferenca;

    public CurvaTurnoDTO(LocalTime hora, double metaAcumulada, int producaoAcumulada, double diferenca) {
        this.hora = hora;
        this.metaAcumulada = metaAcumulada;
        this.producaoAcumulada = producaoAcumulada;
        this.diferenca = diferenca;
    }

    public LocalTime getHora() {
        return hora;
    }

    public double getMetaAcumulada() {
        return metaAcumulada;
    }

    public int getProducaoAcumulada() {
        return producaoAcumulada;
    }

    public double getDiferenca() {
        return diferenca;
    }
}
