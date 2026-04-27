package mx.unison.service;

import mx.unison.Database;
import mx.unison.Producto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Productos.
 * Contiene toda la lógica de negocio relacionada con productos:
 * validaciones, reglas, filtrado y delegación a la BD.
 * Los controladores JavaFX NO acceden a Database directamente.
 */
public class ProductoService {

    private final Database db;

    public ProductoService(Database db) {
        this.db = db;
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    /** Devuelve todos los productos ordenados por nombre. */
    public List<Producto> listarTodos() {
        return db.listProductos();
    }

    /**
     * Filtra la lista por nombre o descripción (case-insensitive).
     * Si el filtro está vacío devuelve todos.
     */
    public List<Producto> buscar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String f = filtro.trim().toLowerCase();
        return listarTodos().stream()
                .filter(p -> (p.nombre    != null && p.nombre.toLowerCase().contains(f))
                          || (p.descripcion != null && p.descripcion.toLowerCase().contains(f))
                          || (p.almacenNombre != null && p.almacenNombre.toLowerCase().contains(f)))
                .collect(Collectors.toList());
    }

    // ── Comandos ─────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo producto tras validar los campos obligatorios.
     * @throws IllegalArgumentException si algún campo es inválido.
     */
    public void agregar(String nombre, String descripcion,
                        String cantidadStr, String precioStr,
                        String almacenIdStr, String usuarioActual) {

        // Validaciones
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria.");
        if (cantidadStr == null || cantidadStr.isBlank())
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        if (precioStr == null || precioStr.isBlank())
            throw new IllegalArgumentException("El precio es obligatorio.");

        int cantidad;
        double precio;
        int almacenId;

        try {
            cantidad = Integer.parseInt(cantidadStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero.");
        }
        try {
            precio = Double.parseDouble(precioStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
        try {
            almacenId = (almacenIdStr == null || almacenIdStr.isBlank())
                    ? 0 : Integer.parseInt(almacenIdStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID de almacén debe ser un número entero.");
        }

        if (cantidad < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        if (precio < 0)
            throw new IllegalArgumentException("El precio no puede ser negativo.");

        Producto p = new Producto();
        p.nombre      = nombre.trim();
        p.descripcion = descripcion.trim();
        p.cantidad    = cantidad;
        p.precio      = precio;
        p.almacenId   = almacenId;

        db.insertProducto(p, usuarioActual);
    }

    /**
     * Actualiza un producto existente.
     * @throws IllegalArgumentException si algún campo es inválido.
     */
    public void actualizar(Producto producto, String usuarioActual) {
        if (producto == null)
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        if (producto.nombre == null || producto.nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        db.updateProducto(producto, usuarioActual);
    }

    /**
     * Elimina un producto por ID.
     * @throws IllegalArgumentException si no se seleccionó ninguno.
     */
    public void eliminar(Producto producto) {
        if (producto == null)
            throw new IllegalArgumentException("Debes seleccionar un producto para eliminar.");
        db.deleteProducto(producto.id);
    }
}
