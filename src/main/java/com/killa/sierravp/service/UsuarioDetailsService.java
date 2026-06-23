package com.killa.sierravp.service;

import com.killa.sierravp.domain.Usuario;
import com.killa.sierravp.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Carga el usuario para Spring Security usando el correo como nombre de login.
 * El rol se deriva del discriminador de la entidad (alumno/profesor/admin).
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    @Transactional(value = "txTransactionManager", readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        String role = "ROLE_" + u.getRol().toUpperCase();
        return new User(u.getCorreo(), u.getPassword(),
                List.of(new SimpleGrantedAuthority(role)));
    }
}
