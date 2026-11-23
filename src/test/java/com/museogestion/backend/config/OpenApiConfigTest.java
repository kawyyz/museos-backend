package com.museogestion.backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.cors.origins=http://localhost:5173",
        "app.jwt.secret=VGhpc0lzQVNlY3JldEtleUZvclRlc3RzMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OpenApiConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generarDocumentacionOpenApiCorrectamente() throws Exception {
        String respuesta = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode documento = objectMapper.readTree(respuesta);

        assertEquals("API MuseoGestión", documento.path("info").path("title").asText());
        assertEquals("1.0", documento.path("info").path("version").asText());

        JsonNode bearerAuth = documento.path("components")
                .path("securitySchemes")
                .path("bearerAuth");

        assertEquals("http", bearerAuth.path("type").asText());
        assertEquals("bearer", bearerAuth.path("scheme").asText());
        assertEquals("JWT", bearerAuth.path("bearerFormat").asText());
    }
}
