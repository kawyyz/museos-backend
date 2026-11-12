package com.museogestion.backend.service;

import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.model.Museo;
import com.museogestion.backend.repository.MuseoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MuseoService {

    private final MuseoRepository repo;

    public MuseoService(MuseoRepository repo) {
        this.repo = repo;
    }

    public List<Museo> listar(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return repo.findAll();
        }
        return repo.findByNombreContainingIgnoreCaseOrZonaContainingIgnoreCase(busqueda, busqueda);
    }

    public Museo obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el museo con id " + id));
    }

    public Museo crear(Museo museo) {
        museo.setId(null);
        return repo.save(museo);
    }

    public Museo actualizar(Long id, Museo datos) {
        Museo existente = obtener(id);
        existente.setNombre(datos.getNombre());
        existente.setSigla(datos.getSigla());
        existente.setZona(datos.getZona());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecioEntrada(datos.getPrecioEntrada());
        existente.setCapacidadDiaria(datos.getCapacidadDiaria());
        existente.setActivo(datos.isActivo());
        return repo.save(existente);
    }

    public void eliminar(Long id) {
        obtener(id);
        repo.deleteById(id);
    }
}