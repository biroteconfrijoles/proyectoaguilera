package mx.unison.service;

import mx.unison.Almacen;
import mx.unison.Database;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Almacenes.
 * Contiene toda la lógica de negocio relacionada con almacenes:
 * validaciones, reglas, filtrado y delegación a la BD.
 */
public class AlmacenService {

    private final Database db;

    public AlmacenService(Database db) {
        this.db = db;
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    /** Devuelve todos los almacenes ordenados por nombre. */
    public List<Almacen> listarTodos() {
        return db.listAlmacenes();
    }

    /**
     * Filtra la lista por nombre o ubicación (case-insensitive).
     * Si el filtro está vacío devuelve todos.
     */
    public List<Almacen> buscar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String f = filtro.trim().toLowerCase();
        return listarTodos().stream()
                .filter(a -> (a.nombre    != null && a.nombre.toLowerCase().contains(f))
                          || (a.ubicacion != null && a.ubicacion.toLowerCase().contains(f)))
                .collect(Collectors.toList());
    }

    // ── Comandos ─────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo almacén tras validar los campos obligatorios.
     * @throws IllegalArgumentException si algún campo es inválido.
     */
    public void agregar(String nombre, String ubicacion, String usuarioActual) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del almacén es obligatorio.");
        if (ubicacion == null || ubicacion.isBlank())
            throw new IllegalArgumentException("La ubicación es obligatoria.");

        db.insertAlmacen(nombre.trim(), ubicacion.trim(), usuarioActual);
    }

    /**
     * Actualiza un almacén existente.
     * @throws IllegalArgumentException si el nombre está vacío.
     */
    public void actualizar(int id, String nombre, String ubicacion, String usuarioActual) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del almacén es obligatorio.");
        if (ubicacion == null || ubicacion.isBlank())
            throw new IllegalArgumentException("La ubicación es obligatoria.");

        db.updateAlmacen(id, nombre.trim(), ubicacion.trim(), usuarioActual);
    }

    /**
     * Elimina un almacén.
     * @throws IllegalArgumentException si no se seleccionó ninguno.
     */
    public void eliminar(Almacen almacen) {
        if (almacen == null)
            throw new IllegalArgumentException("Debes seleccionar un almacén para eliminar.");
        db.deleteAlmacen(almacen.id);
    }
}
