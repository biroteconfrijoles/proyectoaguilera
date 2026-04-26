package mx.unison;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase modelo Usuario.
 *
 * Cubre:
 *  - Instanciación y valores por defecto
 *  - Asignación y lectura de campos nombre y rol
 *  - Valores de rol válidos según el manual (ADMIN, PRODUCTOS, ALMACENES)
 *  - Independencia entre instancias
 */
@DisplayName("Usuario – Clase modelo")
class UsuarioTest {

    // ── Instanciación ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Se puede crear una instancia de Usuario sin argumentos")
    void constructor_creaInstanciaVacia() {
        assertDoesNotThrow(() -> new Usuario());
    }

    @Test
    @DisplayName("Los campos nombre y rol son null al crear sin inicializar")
    void camposDefecto_sonNull() {
        Usuario u = new Usuario();
        assertNull(u.nombre, "nombre debe ser null por defecto");
        assertNull(u.rol,    "rol debe ser null por defecto");
    }

    // ── Asignación y lectura ─────────────────────────────────────────────────

    @Test
    @DisplayName("Se puede asignar y leer el campo nombre")
    void asignarNombre_leerNombre() {
        Usuario u = new Usuario();
        u.nombre = "ADMIN";
        assertEquals("ADMIN", u.nombre);
    }

    @Test
    @DisplayName("Se puede asignar y leer el campo rol")
    void asignarRol_leerRol() {
        Usuario u = new Usuario();
        u.rol = "PRODUCTOS";
        assertEquals("PRODUCTOS", u.rol);
    }

    @Test
    @DisplayName("Se pueden asignar nombre y rol simultáneamente")
    void asignarAmboscampos() {
        Usuario u = new Usuario();
        u.nombre = "ALMACENES";
        u.rol    = "ALMACENES";
        assertEquals("ALMACENES", u.nombre);
        assertEquals("ALMACENES", u.rol);
    }

    // ── Roles válidos según el manual ────────────────────────────────────────

    @Test
    @DisplayName("Rol ADMIN es un valor válido")
    void rol_ADMIN_esValido() {
        Usuario u = new Usuario();
        u.nombre = "ADMIN";
        u.rol    = "ADMIN";
        assertEquals("ADMIN", u.rol);
    }

    @Test
    @DisplayName("Rol PRODUCTOS es un valor válido")
    void rol_PRODUCTOS_esValido() {
        Usuario u = new Usuario();
        u.nombre = "PRODUCTOS";
        u.rol    = "PRODUCTOS";
        assertEquals("PRODUCTOS", u.rol);
    }

    @Test
    @DisplayName("Rol ALMACENES es un valor válido")
    void rol_ALMACENES_esValido() {
        Usuario u = new Usuario();
        u.nombre = "ALMACENES";
        u.rol    = "ALMACENES";
        assertEquals("ALMACENES", u.rol);
    }

    // ── Distinción entre instancias ──────────────────────────────────────────

    @Test
    @DisplayName("Dos instancias de Usuario son independientes entre sí")
    void dosInstancias_sonIndependientes() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        u1.nombre = "ADMIN";
        u1.rol    = "ADMIN";
        u2.nombre = "PRODUCTOS";
        u2.rol    = "PRODUCTOS";

        assertEquals("ADMIN",    u1.nombre);
        assertEquals("PRODUCTOS", u2.nombre);
        assertNotSame(u1, u2, "Deben ser objetos distintos en memoria");
    }

    @Test
    @DisplayName("Modificar una instancia no afecta a la otra")
    void modificarUnaInstancia_noAfectaOtra() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        u1.nombre = "original";
        u2.nombre = u1.nombre;   // copia del valor
        u1.nombre = "modificado";

        assertEquals("original",  u2.nombre, "u2 no debería cambiar cuando u1 cambia");
        assertEquals("modificado", u1.nombre);
    }

    // ── Valores especiales ───────────────────────────────────────────────────

    @Test
    @DisplayName("El campo nombre acepta cadenas con espacios")
    void nombre_aceptaCadenasConEspacios() {
        Usuario u = new Usuario();
        u.nombre = "Juan Pérez";
        assertEquals("Juan Pérez", u.nombre);
    }

    @Test
    @DisplayName("El campo nombre acepta cadena vacía")
    void nombre_aceptaCadenaVacia() {
        Usuario u = new Usuario();
        u.nombre = "";
        assertEquals("", u.nombre);
        assertNotNull(u.nombre);
    }

    @Test
    @DisplayName("Los campos son public y accesibles directamente")
    void campos_sonPublicos_accesiblesDirectamente() {
        // Este test verifica que los campos son public (compilaría si no lo fueran)
        Usuario u = new Usuario();
        u.nombre = "test";
        u.rol    = "test";
        assertNotNull(u.nombre);
        assertNotNull(u.rol);
    }
}
