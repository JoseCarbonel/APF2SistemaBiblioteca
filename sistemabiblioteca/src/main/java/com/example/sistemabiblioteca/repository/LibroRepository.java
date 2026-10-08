package com.example.sistemabiblioteca.repository;

import com.example.sistemabiblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}
