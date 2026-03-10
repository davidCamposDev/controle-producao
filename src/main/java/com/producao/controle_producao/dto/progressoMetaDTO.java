package com.producao.controle_producao.dto;

public class progressoMetaDTO {
    private int producaoAtual;
    private int metaDiaria;
    private double percentual;

    public progressoMetaDTO(int producaoAtual, int metaDiaria, double percentual) {
        this.producaoAtual = producaoAtual;
        this.metaDiaria = metaDiaria;
        this.percentual = percentual;
    }

    public int getProducaoAtual() {
        return producaoAtual;
    }

    public int getMetaDiaria() {
        return metaDiaria;
    }

    public double getPercentual() {
        return percentual;
    }
}

