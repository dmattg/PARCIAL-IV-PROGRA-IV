package com.prograiv.parcial4;

import com.prograiv.parcial4.model.Usuario;
import com.prograiv.parcial4.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Parcial4Application {

    public static void main(String[] args) {
        SpringApplication.run(Parcial4Application.class, args);
    }

    // Este método se ejecuta automáticamente al arrancar la aplicación
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario USER
            if (usuarioRepository.findByUsername("user").isEmpty()) {
                Usuario user = new Usuario(null, "user", passwordEncoder.encode("1234"), "user@test.com", null, null, "ROLE_USER", true, false);
                usuarioRepository.save(user);
            }
            // Usuario ADMIN
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario(null, "admin", passwordEncoder.encode("admin"), "admin@test.com", null, null, "ROLE_ADMIN", true, false);
                usuarioRepository.save(admin);
            }
        };
    }
}
