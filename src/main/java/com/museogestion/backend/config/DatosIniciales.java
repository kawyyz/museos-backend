package com.museogestion.backend.config;

import com.museogestion.backend.model.Museo;
import com.museogestion.backend.repository.MuseoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarMuseos(MuseoRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }
            repo.save(museo("AC", "Museo de Arte Contemporáneo", "Santiago Centro",
                    "Colecciones de arte moderno y exposiciones temporales de artistas nacionales.", 5000, 200));
            repo.save(museo("HN", "Museo de Historia Natural", "Providencia",
                    "Fósiles, minerales y muestras de flora y fauna de la región.", 4500, 150));
            repo.save(museo("CT", "Museo de Ciencia y Tecnología", "Ñuñoa",
                    "Salas interactivas sobre física, astronomía e innovación tecnológica.", 6000, 180));
            repo.save(museo("AV", "Museo de Artes Visuales", "Lastarria",
                    "Fotografía, escultura y muestras itinerantes de artistas emergentes.", 4000, 120));
        };
    }

    private Museo museo(String sigla, String nombre, String zona, String desc, int precio, int capacidad) {
        Museo m = new Museo();
        m.setSigla(sigla);
        m.setNombre(nombre);
        m.setZona(zona);
        m.setDescripcion(desc);
        m.setPrecioEntrada(precio);
        m.setCapacidadDiaria(capacidad);
        return m;
    }
}