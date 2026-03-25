package com.producao.controle_producao.config;

import com.producao.controle_producao.entity.Usuario;
import com.producao.controle_producao.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (usuarioRepository.count() == 0) {
            Usuario u = new Usuario();
            u.setEmail("admin@admin.com");
            u.setSenha(passwordEncoder.encode("123456"));
            usuarioRepository.save(u);

            System.out.println("Usuário admin criado!");
        }
    }
}
