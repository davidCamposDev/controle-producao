package com.producao.controle_producao.config;

import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.entity.Turno;
import com.producao.controle_producao.entity.Usuario;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import com.producao.controle_producao.repository.TurnoRepository;
import com.producao.controle_producao.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GradeHorarioRepository repo;

    public DataInitializer(GradeHorarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {

        // SEG → QUI
        String[] dias = {"SEGUNDA", "TERCA", "QUARTA", "QUINTA"};

        for (String dia : dias) {

            // 🔥 TURNO 2
            salvarSeNaoExiste("17:00", 57, 228.0, dia, 2L);
            salvarSeNaoExiste("18:00", 60, 240.0, dia, 2L);
            salvarSeNaoExiste("19:00", 50, 200.0, dia, 2L);
            salvarSeNaoExiste("20:00", 60, 240.0, dia, 2L);
            salvarSeNaoExiste("21:00", 0, 0.0, dia, 2L);
            salvarSeNaoExiste("22:00", 60, 240.0, dia, 2L);
            salvarSeNaoExiste("23:00", 60, 240.0, dia, 2L);
            salvarSeNaoExiste("00:00", 50, 200.0, dia, 2L);
            salvarSeNaoExiste("01:00", 60, 240.0, dia, 2L);
            salvarSeNaoExiste("02:00", 20, 80.0, dia, 2L);
        }

        // 🔥 SEXTA
        String dia = "SEXTA";

        salvarSeNaoExiste("16:00", 57, 228.0, dia, 2L);
        salvarSeNaoExiste("17:00", 60, 240.0, dia, 2L);
        salvarSeNaoExiste("18:00", 50, 200.0, dia, 2L);
        salvarSeNaoExiste("19:00", 60, 240.0, dia, 2L);
        salvarSeNaoExiste("20:00", 0, 0.0, dia, 2L);
        salvarSeNaoExiste("21:00", 60, 240.0, dia, 2L);
        salvarSeNaoExiste("22:00", 60, 240.0, dia, 2L);
        salvarSeNaoExiste("23:00", 50, 200.0, dia, 2L);
        salvarSeNaoExiste("00:00", 35, 140.0, dia, 2L);

        System.out.println("🔥 Dados verificados/inseridos sem duplicação!");
    }

    private void salvarSeNaoExiste(String hora, int minutos, Double meta, String dia, Long turnoId) {

        LocalTime horaInicio = LocalTime.parse(hora + ":00");

        boolean existe = repo.existsByHoraInicioAndDiaSemanaAndTurnoId(horaInicio, dia, turnoId);

        if (!existe) {
            GradeHorario g = new GradeHorario();

            g.setHoraInicio(horaInicio);
            g.setMinutosPlanejados(minutos);
            g.setMeta(meta);
            g.setDiaSemana(dia);

            Turno turno = new Turno();
            turno.setId(turnoId);
            g.setTurno(turno);

            repo.save(g);
        }
    }
}
