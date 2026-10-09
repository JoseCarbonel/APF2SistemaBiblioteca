**Sistema de Gestión de Biblioteca - API REST (APF2)**

Este proyecto es una aplicación Spring Boot desarrollada bajo los principios de la arquitectura RESTful, asegurada mediante Spring Security + JWT y con persistencia en PostgreSQL (Neon), probada mediante la metodología TDD (Test-Driven Development).

**Arquitectura del Proyecto**

El sistema está construido siguiendo una separación limpia por capas:

Model (com.example.sistemabiblioteca.model): Define las entidades principales (Libro, Usuario, Prestamo, Resena) mapeadas a tablas en PostgreSQL mediante JPA/Hibernate.

Controller (com.example.sistemabiblioteca.controller): Expone la API REST (@RestController) mediante ResponseEntity, gestionando los verbos HTTP (GET, POST, PUT, DELETE) y mapeando datos con @PathVariable y @RequestBody.

Security (com.example.sistemabiblioteca.security): Implementa la autenticación stateless basada en tokens JWT (JwtFilter, SecurityConfig) para la protección de endpoints.

Pruebas Unitarias TDD (src/test/java/...): Cobertura mediante casos de prueba aislados en LibroControllerTest y PrestamoControllerTest.

**Endpoints de la API (9 Operaciones REST)**

**Autenticación (/auth)**

POST /auth/login — Autenticarse para obtener el token JWT (200 OK / 401 Unauthorized).

**Gestión de Libros (/api/libros)**

POST /api/libros — Registrar un nuevo libro (201 Created / 400 Bad Request / 401 Unauthorized).

GET /api/libros — Listar todos los libros (200 OK / 401 Unauthorized).

GET /api/libros/{id} — Buscar un libro por ID (200 OK / 404 Not Found / 400 Bad Request / 401 Unauthorized).

POST /api/libros/{id}/reseñas — Agregar una reseña a un libro (201 Created / 404 Not Found / 400 Bad Request / 401 Unauthorized).

**Gestión de Préstamos (/api/prestamos)**

POST /api/prestamos — Registrar un préstamo (201 Created / 404 Not Found / 400 Bad Request / 401 Unauthorized).

GET /api/prestamos/usuario/{usuarioId} — Listar préstamos por usuario (200 OK / 404 Not Found / 401 Unauthorized).

PUT /api/prestamos/{id}/devolver — Devolver un libro prestado (200 OK / 404 Not Found / 400 Bad Request / 401 Unauthorized).

DELETE /api/prestamos/{id} — Cancelar/Eliminar un préstamo (204 No Content / 404 Not Found / 400 Bad Request / 401 Unauthorized).

**Ejecución del Proyecto**
Iniciar la aplicación:
./gradlew bootRun

**Ejecutar la suite completa de pruebas TDD:**
./gradlew test
