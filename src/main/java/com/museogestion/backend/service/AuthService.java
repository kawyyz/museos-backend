package com.museogestion.backend.service;

import com.museogestion.backend.dto.AuthDto.AuthResponse;
import com.museogestion.backend.dto.AuthDto.LoginRequest;
import com.museogestion.backend.dto.AuthDto.RefreshRequest;
import com.museogestion.backend.dto.AuthDto.RegistroRequest;
import com.museogestion.backend.exception.ConflictoException;
import com.museogestion.backend.exception.CredencialesInvalidasException;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import com.museogestion.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse registrar(RegistroRequest r) {
        String correo = r.correo().trim().toLowerCase();
        if (repo.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe una cuenta con ese correo");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setCorreo(correo);
        u.setPassword(encoder.encode(r.password()));
        u.setTelefono(r.telefono());
        u.setCiudad(r.ciudad());
        u.setRol(Rol.CLIENTE);
        return respuesta(repo.save(u));
    }

    public AuthResponse login(LoginRequest r) {
        Usuario u = repo.findByCorreoIgnoreCase(r.correo().trim())
                .orElseThrow(CredencialesInvalidasException::new);
        if (!encoder.matches(r.password(), u.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        return respuesta(u);
    }

    public AuthResponse refrescar(RefreshRequest r) {
        Claims claims;
        try {
            claims = jwt.leer(r.refreshToken());
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException("Refresh token inválido o vencido");
        }
        if (!"refresh".equals(claims.get("tipo", String.class))) {
            throw new CredencialesInvalidasException("El token entregado no es un refresh token");
        }
        Usuario u = repo.findByCorreoIgnoreCase(claims.getSubject())
                .orElseThrow(() -> new CredencialesInvalidasException("Refresh token inválido o vencido"));
        return respuesta(u);
    }

    private AuthResponse respuesta(Usuario u) {
        return new AuthResponse(
                jwt.generarAccessToken(u),
                jwt.generarRefreshToken(u),
                "Bearer",
                jwt.getAccessSegundos(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol().name());
    }
}