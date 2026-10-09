package com.example.sistemabiblioteca.controller;

import com.example.sistemabiblioteca.model.Libro;
import com.example.sistemabiblioteca.model.Usuario;
import com.example.sistemabiblioteca.repository.LibroRepository;
import com.example.sistemabiblioteca.repository.UsuarioRepository;
import com.example.sistemabiblioteca.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class LibroControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;
    private Long libroId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        libroRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario y token para los tests protegidos
        Usuario user = new Usuario(null, "admin", "123", "admin@mail.com", "ADMIN");
        usuarioRepository.save(user);
        token = "Bearer " + jwtUtil.generateToken("admin");

        // Crear libro base para pruebas
        Libro libro = new Libro(null, "Cien Años de Soledad", "978-0307474728", "Novela", 10);
        Libro guardado = libroRepository.save(libro);
        libroId = guardado.getId();
    }

    // Endpoint 1: Crear Libro (POST /api/libros)
    @Test
    void testCrearExito() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"Don Quijote\", \"isbn\": \"123456\", \"categoria\": \"Clasico\", \"stock\": 5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCrearErrorSinTitulo() throws Exception {
        mockMvc.perform(post("/api/libros")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"\", \"isbn\": \"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    // Endpoint 2: Listar Libros (GET /api/libros)
    @Test
    void testListarExito() throws Exception {
        mockMvc.perform(get("/api/libros")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListarSinTokenNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isForbidden());
    }

    // Endpoint 3: Buscar Libro por ID (GET /api/libros/{id})
    @Test
    void testBuscarPorIdExito() throws Exception {
        mockMvc.perform(get("/api/libros/" + libroId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Cien Años de Soledad"));
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/libros/999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    // Endpoint 4: Agregar Reseña a un Libro (POST /api/libros/{id}/reseñas)
    @Test
    void testAgregarResenaExito() throws Exception {
        mockMvc.perform(post("/api/libros/" + libroId + "/reseñas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\": \"Excelente libro\", \"calificacion\": 5, \"fecha\": \"2026-10-08\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void testAgregarResenaCalificacionInvalida() throws Exception {
        mockMvc.perform(post("/api/libros/" + libroId + "/reseñas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentario\": \"Malo\", \"calificacion\": 10}"))
                .andExpect(status().isBadRequest());
    }
}
