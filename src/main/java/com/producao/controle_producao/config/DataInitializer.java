package com.producao.controle_producao.config;

import com.producao.controle_producao.entity.GradeHorario;
import com.producao.controle_producao.entity.Turno;
import com.producao.controle_producao.entity.Usuario;
import com.producao.controle_producao.repository.GradeHorarioRepository;
import com.producao.controle_producao.repository.TurnoRepository;
import com.producao.controle_producao.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private GradeHorarioRepository gradeHorarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // 🔐 USUÁRIO
        if (usuarioRepository.count() == 0) {
            Usuario u = new Usuario();
            u.setEmail("admin@admin.com");
            u.setSenha(passwordEncoder.encode("123456"));
            usuarioRepository.save(u);
        }

        // 🏭 TURNOS
        if (turnoRepository.count() == 0) {

            Turno t1 = new Turno();
            t1.setNome("1º Turno");
            turnoRepository.save(t1);

            Turno t2 = new Turno();
            t2.setNome("2º Turno");
            turnoRepository.save(t2);

            // ⏱️ HORÁRIOS DO 1º TURNO (07–16)
            for (int h = 7; h <= 16; h++) {
                GradeHorario g = new GradeHorario();
                g.setHoraInicio(LocalTime.of(h, 0));
                g.setTurno(t1);
                g.setMinutosPlanejados(60);
                gradeHorarioRepository.save(g);
            }

            // ⏱️ HORÁRIOS DO 2º TURNO (17–02)
            for (int h = 17; h <= 23; h++) {
                GradeHorario g = new GradeHorario();
                g.setHoraInicio(LocalTime.of(h, 0));
                g.setTurno(t2);
                g.setMinutosPlanejados(60);
                gradeHorarioRepository.save(g);
            }

            for (int h = 0; h <= 2; h++) {
                GradeHorario g = new GradeHorario();
                g.setHoraInicio(LocalTime.of(h, 0));
                g.setTurno(t2);
                g.setMinutosPlanejados(60);
                gradeHorarioRepository.save(g);
            }
        }
    }
}
