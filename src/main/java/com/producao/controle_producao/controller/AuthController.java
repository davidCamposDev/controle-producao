package com.producao.controle_producao.controller;

import com.producao.controle_producao.dto.LoginDTO;
import com.producao.controle_producao.entity.Usuario;
import com.producao.controle_producao.repository.UsuarioRepository;
import com.producao.controle_producao.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {

        Usuario user = usuarioRepository.findByEmail(dto.email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senha, user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return ResponseEntity.ok(jwtService.gerarToken(user.getEmail()));
    }
}