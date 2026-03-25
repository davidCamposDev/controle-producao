package com.producao.controle_producao.dto;

public class progressoMetaDTO {
    private int producaoAtual;
    private double metaTurno;
    private double percentual;

    public progressoMetaDTO(int producaoAtual, double metaTurno, double percentual) {
        this.producaoAtual = producaoAtual;
        this.metaTurno = metaTurno;
        this.percentual = percentual;
    }

    public int getProducaoAtual() {
        return producaoAtual;
    }

    public double getMetaTurno() {
        return metaTurno;
    }

    public double getPercentual() {
        return percentual;
    }
}

