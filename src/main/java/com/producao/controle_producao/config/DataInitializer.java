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
            salvarSeNaoExiste("07:00", 57, 228.0, dia, 1L);
            salvarSeNaoExiste("08:00", 60, 240.0, dia, 1L);
            salvarSeNaoExiste("09:00", 50, 200.0, dia, 1L);
            salvarSeNaoExiste("10:00", 60, 240.0, dia, 1L);
            salvarSeNaoExiste("11:00", 15, 60.0, dia, 1L);
            salvarSeNaoExiste("12:00", 45, 180.0, dia, 1L);
            salvarSeNaoExiste("13:00", 60, 240.0, dia, 1L);
            salvarSeNaoExiste("14:00", 60, 240.0, dia, 1L);
            salvarSeNaoExiste("15:00", 50, 200.0, dia, 1L);
            salvarSeNaoExiste("16:00", 60, 240.0, dia, 1L);
        }

        // 🔥 SEXTA
        String dia = "SEXTA";

        salvarSeNaoExiste("07:00", 57, 228.0, dia, 1L);
        salvarSeNaoExiste("08:00", 60, 240.0, dia, 1L);
        salvarSeNaoExiste("09:00", 50, 200.0, dia, 1L);
        salvarSeNaoExiste("10:00", 60, 240.0, dia, 1L);
        salvarSeNaoExiste("11:00", 15, 60.0, dia, 1L);
        salvarSeNaoExiste("12:00", 45, 180.0, dia, 1L);
        salvarSeNaoExiste("13:00", 60, 240.0, dia, 1L);
        salvarSeNaoExiste("14:00", 50, 200.0, dia, 1L);
        salvarSeNaoExiste("15:00", 60, 240.0, dia, 1L);

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
