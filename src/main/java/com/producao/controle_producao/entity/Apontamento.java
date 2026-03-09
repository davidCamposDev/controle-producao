package com.producao.controle_producao.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"data", "grade_horario_id"})
        }
)
public class Apontamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "grade_horario_id")
    private GradeHorario gradeHorario;

    private Integer quantidadeProduzida;

    private Double percentual;

    private String observacao;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public GradeHorario getGradeHorario() {
        return gradeHorario;
    }

    public void setGradeHorario(GradeHorario gradeHorario) {
        this.gradeHorario = gradeHorario;
    }

    public Integer getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(Integer quantidadeProduzida) {
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}