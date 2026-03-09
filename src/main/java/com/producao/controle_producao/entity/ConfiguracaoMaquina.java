package com.producao.controle_producao.entity;

import jakarta.persistence.*;

@Entity
public class ConfiguracaoMaquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer capacidadeHora;

    private Double eficienciaDesejada;

    public Long getId() {
        return id;
    }

    public Integer getCapacidadeHora() {
        return capacidadeHora;
    }

    public void setCapacidadeHora(Integer capacidadeHora) {
        this.capacidadeHora = capacidadeHora;
    }

    public Double getEficienciaDesejada() {
        return eficienciaDesejada;
    }

    public void setEficienciaDesejada(Double eficienciaDesejada) {
        this.eficienciaDesejada = eficienciaDesejada;
    }
}
