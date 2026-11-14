package com.museogestion.backend.config;

import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UsuariosIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repo,
                                     PasswordEncoder encoder,
                                     @Value("${app.seed.admin-password}") String passwordAdmin) {
        return args -> {
            if (!repo.existsByCorreoIgnoreCase("admin@museos.cl")) {
                repo.save(usuario("Administrador", "admin@museos.cl", passwordAdmin,
                        "+56 9 0000 0000", "Santiago, Chile", Rol.ADMIN, encoder));
            }
            if (!repo.existsByCorreoIgnoreCase("juan.perez@email.com")) {
                repo.save(usuario("Juan Pérez", "juan.perez@email.com", "Cliente12345",
                        "+56 9 1234 5678", "Santiago, Chile", Rol.CLIENTE, encoder));
            }
        };
    }

    private Usuario usuario(String nombre, String correo, String password, String telefono,
                            String ciudad, Rol rol, PasswordEncoder encoder) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPassword(encoder.encode(password));
        u.setTelefono(telefono);
        u.setCiudad(ciudad);
        u.setRol(rol);
        return u;
    }
}