package com.killa.sierravp.config;

import com.killa.sierravp.domain.Administrativo;
import com.killa.sierravp.repository.UsuarioRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea el usuario administrador al arrancar (si no existe), para resolver el
 * bootstrap: el admin es quien puede ejecutar "Poblar BD", así que debe existir
 * antes del primer login.
 *
 *   admin@unmsm.edu.pe / Admin2026
 */
@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner crearAdminInicial(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        return args -> ensureAdmin(usuarioRepo, encoder);
    }

    @Transactional("txTransactionManager")
    void ensureAdmin(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        if (usuarioRepo.existsByCorreo("admin@unmsm.edu.pe")) return;

        Administrativo admin = new Administrativo();
        admin.setCodigo(1000);
        admin.setPrimerNombre("Admin");
        admin.setSegundoNombre("");
        admin.setPrimerApellido("General");
        admin.setSegundoApellido("");
        admin.setCorreo("admin@unmsm.edu.pe");
        admin.setPassword(encoder.encode("Admin2026"));
        usuarioRepo.save(admin);
    }
}
