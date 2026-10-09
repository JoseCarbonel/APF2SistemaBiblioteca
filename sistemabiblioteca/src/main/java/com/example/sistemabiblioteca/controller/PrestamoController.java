package com.example.sistemabiblioteca.controller;

import com.example.sistemabiblioteca.model.Prestamo;
import com.example.sistemabiblioteca.repository.LibroRepository;
import com.example.sistemabiblioteca.repository.PrestamoRepository;
import com.example.sistemabiblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    // Endpoint 5: Registrar Préstamo (POST /api/prestamos)
    @PostMapping
    public ResponseEntity<?> registrarPrestamo(@RequestBody Prestamo prestamo) {
        if (prestamo.getLibroId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El libroId es obligatorio");
        }
        if (prestamo.getUsuarioId() == null || !usuarioRepository.existsById(prestamo.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        if (!libroRepository.existsById(prestamo.getLibroId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Libro no encontrado");
        }

        prestamo.setEstado("PENDIENTE");
        Prestamo guardado = prestamoRepository.save(prestamo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Endpoint 6: Listar Préstamos por Usuario (GET /api/prestamos/usuario/{usuarioId})
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(prestamos);
    }

    // Endpoint 7: Devolver Libro (PUT /api/prestamos/{id}/devolver)
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolverLibro(@PathVariable Long id) {
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);
        if (prestamoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Préstamo no encontrado");
        }

        Prestamo prestamo = prestamoOpt.get();
        if ("DEVUELTO".equals(prestamo.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El préstamo ya se encuentra devuelto");
        }

        prestamo.setEstado("DEVUELTO");
        Prestamo actualizado = prestamoRepository.save(prestamo);
        return ResponseEntity.ok(actualizado);
    }

    // Endpoint 8: Cancelar/Eliminar Préstamo (DELETE /api/prestamos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarPrestamo(@PathVariable Long id) {
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);
        if (prestamoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Préstamo no encontrado");
        }

        Prestamo prestamo = prestamoOpt.get();
        if ("DEVUELTO".equals(prestamo.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede cancelar un préstamo que ya fue devuelto");
        }

        prestamoRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
