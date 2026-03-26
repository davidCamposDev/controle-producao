package com.producao.controle_producao.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class GradeHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    private LocalTime horaInicio;

    private Integer minutosPlanejados;

    private Double meta;

    public Long getId() {
        return id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getMinutosPlanejados() {
        return minutosPlanejados;
    }

    public void setMinutosPlanejados(Integer minutosPlanejados) {
        this.minutosPlanejados = minutosPlanejados;
    }

    public Double getMeta() { return meta;
    }
    public void setMeta(Double meta) {this.meta = meta;}
}
