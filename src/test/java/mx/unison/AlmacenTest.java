package mx.unison;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase modelo Almacen.
 *
 * Cubre:
 *  - Instanciación y valores por defecto
 *  - Asignación y lectura de todos los campos
 *  - Tipos de dato correctos (int para id, String para el resto)
 *  - Independencia entre instancias
 *  - Casos borde: id=0, id negativo, cadenas nulas
 */
@DisplayName("Almacen – Clase modelo")
class AlmacenTest {

    // ── Instanciación ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Se puede crear una instancia de Almacen sin argumentos")
    void constructor_creaInstanciaCorrectamente() {
        assertDoesNotThrow(() -> new Almacen());
    }

    @Test
    @DisplayName("El campo id es 0 (int por defecto) al crear sin inicializar")
    void id_defecto_esCero() {
        Almacen a = new Almacen();
        assertEquals(0, a.id, "id debe valer 0 por defecto en int primitivo");
    }

    @Test
    @DisplayName("Los campos String son null al crear sin inicializar")
    void camposString_defecto_sonNull() {
        Almacen a = new Almacen();
        assertNull(a.nombre,            "nombre debe ser null");
        assertNull(a.ubicacion,         "ubicacion debe ser null");
        assertNull(a.fechaHoraCreacion, "fechaHoraCreacion debe ser null");
        assertNull(a.fechaHoraUltimaMod,"fechaHoraUltimaMod debe ser null");
        assertNull(a.ultimoUsuario,     "ultimoUsuario debe ser null");
    }

    // ── Asignación y lectura de todos los campos ─────────────────────────────

    @Test
    @DisplayName("Se puede asignar y leer correctamente el campo id")
    void campo_id_asignacionYLectura() {
        Almacen a = new Almacen();
        a.id = 42;
        assertEquals(42, a.id);
    }

    @Test
    @DisplayName("Se puede asignar y leer correctamente el campo nombre")
    void campo_nombre_asignacionYLectura() {
        Almacen a = new Almacen();
        a.nombre = "Almacén Central";
        assertEquals("Almacén Central", a.nombre);
    }

    @Test
    @DisplayName("Se puede asignar y leer correctamente el campo ubicacion")
    void campo_ubicacion_asignacionYLectura() {
        Almacen a = new Almacen();
        a.ubicacion = "Hermosillo, Sonora";
        assertEquals("Hermosillo, Sonora", a.ubicacion);
    }

    @Test
    @DisplayName("Se puede asignar y leer correctamente fechaHoraCreacion")
    void campo_fechaHoraCreacion_asignacionYLectura() {
        Almacen a = new Almacen();
        a.fechaHoraCreacion = "2026-03-20 10:30:00";
        assertEquals("2026-03-20 10:30:00", a.fechaHoraCreacion);
    }

    @Test
    @DisplayName("Se puede asignar y leer correctamente fechaHoraUltimaMod")
    void campo_fechaHoraUltimaMod_asignacionYLectura() {
        Almacen a = new Almacen();
        a.fechaHoraUltimaMod = "2026-03-20 12:00:00";
        assertEquals("2026-03-20 12:00:00", a.fechaHoraUltimaMod);
    }

    @Test
    @DisplayName("Se puede asignar y leer correctamente el campo ultimoUsuario")
    void campo_ultimoUsuario_asignacionYLectura() {
        Almacen a = new Almacen();
        a.ultimoUsuario = "ADMIN";
        assertEquals("ADMIN", a.ultimoUsuario);
    }

    @Test
    @DisplayName("Se pueden asignar todos los campos a la vez")
    void todosLosCampos_asignacionSimultanea() {
        Almacen a = new Almacen();
        a.id                  = 1;
        a.nombre              = "Almacén Norte";
        a.ubicacion           = "Cd. Obregón";
        a.fechaHoraCreacion   = "2026-01-01 08:00:00";
        a.fechaHoraUltimaMod  = "2026-03-01 09:00:00";
        a.ultimoUsuario       = "ALMACENES";

        assertEquals(1,                    a.id);
        assertEquals("Almacén Norte",      a.nombre);
        assertEquals("Cd. Obregón",        a.ubicacion);
        assertEquals("2026-01-01 08:00:00",a.fechaHoraCreacion);
        assertEquals("2026-03-01 09:00:00",a.fechaHoraUltimaMod);
        assertEquals("ALMACENES",          a.ultimoUsuario);
    }

    // ── Independencia entre instancias ───────────────────────────────────────

    @Test
    @DisplayName("Dos instancias de Almacen son independientes entre sí")
    void dosInstancias_sonIndependientes() {
        Almacen a1 = new Almacen();
        Almacen a2 = new Almacen();
        a1.id     = 1;  a1.nombre = "Norte";
        a2.id     = 2;  a2.nombre = "Sur";

        assertEquals(1, a1.id);    assertEquals("Norte", a1.nombre);
        assertEquals(2, a2.id);    assertEquals("Sur",   a2.nombre);
        assertNotSame(a1, a2);
    }

    // ── Casos borde ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("El campo id acepta valor 0 (sin asignar en BD)")
    void campo_id_acepta_cero() {
        Almacen a = new Almacen();
        a.id = 0;
        assertEquals(0, a.id);
    }

    @Test
    @DisplayName("El campo id acepta valores negativos (caso inválido, pero no falla)")
    void campo_id_acepta_negativo() {
        Almacen a = new Almacen();
        a.id = -1;
        assertEquals(-1, a.id);
    }

    @Test
    @DisplayName("El campo nombre acepta cadena vacía")
    void campo_nombre_aceptaCadenaVacia() {
        Almacen a = new Almacen();
        a.nombre = "";
        assertEquals("", a.nombre);
        assertNotNull(a.nombre);
    }

    @Test
    @DisplayName("El campo ubicacion puede ser null (almacén sin ubicación registrada)")
    void campo_ubicacion_puedeSerNull() {
        Almacen a = new Almacen();
        a.nombre    = "Sin Ubicación";
        a.ubicacion = null;
        assertNull(a.ubicacion);
        // Nombre sigue intacto
        assertEquals("Sin Ubicación", a.nombre);
    }
}
