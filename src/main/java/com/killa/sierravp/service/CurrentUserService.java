package com.killa.sierravp.service;

import com.killa.sierravp.domain.Usuario;
import com.killa.sierravp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

/** Helper para obtener el {@link Usuario} autenticado a partir del correo (principal). */
@Service
public class CurrentUserService {

    private final UsuarioRepository usuarioRepo;

    public CurrentUserService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public Usuario actual(Principal principal) {
        if (principal == null) return null;
        return usuarioRepo.findByCorreo(principal.getName()).orElse(null);
    }
}
