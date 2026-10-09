package com.example.sistemabiblioteca.controller;

import com.example.sistemabiblioteca.model.Libro;
import com.example.sistemabiblioteca.model.Prestamo;
import com.example.sistemabiblioteca.model.Usuario;
import com.example.sistemabiblioteca.repository.LibroRepository;
import com.example.sistemabiblioteca.repository.PrestamoRepository;
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
public class PrestamoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;
    private Long usuarioId;
    private Long libroId;
    private Long prestamoId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        prestamoRepository.deleteAll();
        libroRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario user = new Usuario(null, "estudiante", "123456", "estudiante@mail.com", "USER");
        Usuario userGuardado = usuarioRepository.save(user);
        usuarioId = userGuardado.getId();

        token = "Bearer " + jwtUtil.generateToken("estudiante");

        Libro libro = new Libro(null, "Spring Boot Guide", "000111", "Sistemas", 3);
        Libro libroGuardado = libroRepository.save(libro);
        libroId = libroGuardado.getId();

        Prestamo p = new Prestamo(null, usuarioId, libroId, "2026-10-08", "PENDIENTE");
        Prestamo pGuardado = prestamoRepository.save(p);
        prestamoId = pGuardado.getId();
    }

    // Endpoint 5: Registrar Préstamo (POST /api/prestamos)
    @Test
    void testRegistrarPrestamoExito() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": " + usuarioId + ", \"libroId\": " + libroId + ", \"fechaPrestamo\": \"2026-10-08\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void testRegistrarPrestamoUsuarioInexistente() throws Exception {
        mockMvc.perform(post("/api/prestamos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 999, \"libroId\": " + libroId + "}"))
                .andExpect(status().isNotFound());
    }

    // Endpoint 6: Listar Préstamos por Usuario (GET /api/prestamos/usuario/{usuarioId})
    @Test
    void testListarPorUsuarioExito() throws Exception {
        mockMvc.perform(get("/api/prestamos/usuario/" + usuarioId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListarPorUsuarioNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/prestamos/usuario/999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    // Endpoint 7: Devolver Libro (PUT /api/prestamos/{id}/devolver)
    @Test
    void testDevolverLibroExito() throws Exception {
        mockMvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"));
    }

    @Test
    void testDevolverYaDevueltoError() throws Exception {
        Prestamo p = prestamoRepository.findById(prestamoId).get();
        p.setEstado("DEVUELTO");
        prestamoRepository.save(p);

        mockMvc.perform(put("/api/prestamos/" + prestamoId + "/devolver")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    // Endpoint 8: Cancelar/Eliminar Préstamo (DELETE /api/prestamos/{id})
    @Test
    void testCancelarPrestamoExito() throws Exception {
        mockMvc.perform(delete("/api/prestamos/" + prestamoId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCancelarPrestamoInexistente() throws Exception {
        mockMvc.perform(delete("/api/prestamos/999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
