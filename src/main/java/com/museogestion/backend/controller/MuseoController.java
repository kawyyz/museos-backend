package com.museogestion.backend.controller;

import com.museogestion.backend.model.Museo;
import com.museogestion.backend.service.MuseoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/museos")
@Tag(name = "Museos", description = "Catálogo de museos")
public class MuseoController {

    private final MuseoService service;

    public MuseoController(MuseoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista los museos, con búsqueda opcional por nombre o zona")
    public List<Museo> listar(@RequestParam(required = false) String q) {
        return service.listar(q);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un museo por su id")
    public Museo obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un museo")
    public ResponseEntity<Museo> crear(@Valid @RequestBody Museo museo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(museo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un museo")
    public Museo actualizar(@PathVariable Long id, @Valid @RequestBody Museo museo) {
        return service.actualizar(id, museo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un museo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}