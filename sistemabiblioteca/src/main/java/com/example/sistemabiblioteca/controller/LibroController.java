package com.example.sistemabiblioteca.controller;

import com.example.sistemabiblioteca.model.Libro;
import com.example.sistemabiblioteca.model.Resena;
import com.example.sistemabiblioteca.repository.LibroRepository;
import com.example.sistemabiblioteca.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ResenaRepository resenaRepository;

    // Endpoint 1: Crear Libro (POST /api/libros)
    @PostMapping
    public ResponseEntity<?> crearLibro(@RequestBody Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El título es obligatorio");
        }
        Libro guardado = libroRepository.save(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Endpoint 2: Listar Libros (GET /api/libros)
    @GetMapping
    public ResponseEntity<List<Libro>> listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        return ResponseEntity.ok(libros);
    }

    // Endpoint 3: Buscar Libro por ID (GET /api/libros/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Libro> libroOpt = libroRepository.findById(id);
        if (libroOpt.isPresent()) {
            return ResponseEntity.ok(libroOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Libro no encontrado");
    }

    // Endpoint 4: Agregar Reseña a un Libro (POST /api/libros/{id}/reseñas)
    @PostMapping("/{id}/reseñas")
    public ResponseEntity<?> agregarResena(@PathVariable Long id, @RequestBody Resena resena) {
        if (!libroRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Libro no encontrado");
        }
        if (resena.getCalificacion() == null || resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La calificación debe estar entre 1 y 5");
        }
        resena.setLibroId(id);
        Resena guardada = resenaRepository.save(resena);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}
