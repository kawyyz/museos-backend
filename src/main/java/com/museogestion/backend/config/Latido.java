package com.museogestion.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

/**
 * Latido (heartbeat): cada 60 segundos escribe una línea en el log con el estado de la aplicación
 * y de la base de datos. En CloudWatch sirve para comprobar que el servicio sigue vivo:
 * si las líneas HEARTBEAT dejan de aparecer, algo anda mal.
 */
@Component
@EnableScheduling
public class Latido {

    private static final Logger log = LoggerFactory.getLogger(Latido.class);

    private final JdbcTemplate jdbc;

    public Latido(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 30_000)
    public void latir() {
        String bd;
        try {
            jdbc.queryForObject("select 1", Integer.class);
            bd = "UP";
        } catch (Exception e) {
            bd = "DOWN";
        }
        long segundos = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        log.info("HEARTBEAT app=UP bd={} uptime_s={}", bd, segundos);
    }
}
