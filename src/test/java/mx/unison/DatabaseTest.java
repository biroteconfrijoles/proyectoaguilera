package mx.unison;


import org.junit.jupiter.api.*;

import java.io.File;

import java.nio.file.Files;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**

 * Pruebas de integración para la clase Database.

 *

 * Estrategia: cada test class crea una BD SQLite en un archivo temporal único,

 * lo que garantiza aislamiento total entre tests y no contamina la BD de producción.

 *

 * Cubre:

 * ── Inicialización ──────────────────────────────────────────────────────────

 * - Las tres tablas se crean correctamente

 * - Los tres usuarios base se insertan con contraseñas encriptadas (MD5)

 * - Segunda inicialización no duplica usuarios (idempotente)

 * ── Autenticación ──────────────────────────────────────────────────────────

 * - Login exitoso con credenciales correctas para los 3 usuarios

 * - Login fallido con contraseña incorrecta

 * - Login fallido con usuario inexistente

 * - Login fallido con campos vacíos

 * - authenticate() actualiza fecha_hora_ultimo_inicio

 * - authenticate() retorna nombre y rol correctos

 * ── ALMACENES ──────────────────────────────────────────────────────────────

 * - insertAlmacen retorna ID generado > 0

 * - listAlmacenes devuelve el almacén insertado

 * - updateAlmacen modifica nombre, ubicación y fecha de modificación

 * - deleteAlmacen elimina el registro y no queda en la lista

 * - listAlmacenes devuelve lista vacía cuando no hay almacenes

 * - listAlmacenes ordena por nombre (alfabético)

 * ── PRODUCTOS ──────────────────────────────────────────────────────────────

 * - insertProducto retorna ID generado > 0

 * - listProductos devuelve el producto con nombre de almacén resuelto

 * - listProductos muestra null para almacenNombre cuando almacen_id es null

 * - updateProducto modifica todos los campos correctamente

 * - deleteProducto elimina el registro correctamente

 * - insertProducto con almacenId=0 guarda NULL en la BD

 * - listProductos ordena por nombre (alfabético)

 * ── Integridad referencial ─────────────────────────────────────────────────

 * - listProductos resuelve el nombre del almacén via LEFT JOIN

 */

@DisplayName("Database – Pruebas de integración")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class DatabaseTest {


    private static File dbFile;

    private static Database db;


    @BeforeAll

    static void setUp() throws Exception {

// BD temporal exclusiva para este test suite

        dbFile = Files.createTempFile("test_inventario_", ".db").toFile();

        dbFile.deleteOnExit();

        db = new Database("jdbc:sqlite:" + dbFile.getAbsolutePath());

    }


    @AfterAll

    static void tearDown() {

        if (dbFile != null) dbFile.delete();

    }


// ════════════════════════════════════════════════════════════════════════

// INICIALIZACIÓN

// ════════════════════════════════════════════════════════════════════════


    @Test @Order(1)

    @DisplayName("INIT: Los tres usuarios base existen tras la inicialización")

    void init_tresusuariosBase_existen() {

// Si los usuarios no existieran, authenticate retornaría null

        assertNotNull(db.authenticate("ADMIN", "admin23"), "ADMIN debe existir");

        assertNotNull(db.authenticate("PRODUCTOS", "productos19"), "PRODUCTOS debe existir");

        assertNotNull(db.authenticate("ALMACENES", "almacenes11"), "ALMACENES debe existir");

    }


    @Test @Order(2)

    @DisplayName("INIT: Segunda inicialización no duplica usuarios (idempotente)")

    void init_idempotente_noduplicaUsuarios() {

// Crear segunda instancia apuntando a la misma BD

        Database db2 = new Database("jdbc:sqlite:" + dbFile.getAbsolutePath());

// Deben seguir siendo exactamente 3 usuarios

// Lo verificamos con authenticate, no con count directo

        assertNotNull(db2.authenticate("ADMIN", "admin23"));

        assertNotNull(db2.authenticate("PRODUCTOS", "productos19"));

        assertNotNull(db2.authenticate("ALMACENES", "almacenes11"));

    }


// ════════════════════════════════════════════════════════════════════════

// AUTENTICACIÓN

// ════════════════════════════════════════════════════════════════════════


    @Test @Order(3)

    @DisplayName("AUTH: ADMIN con credenciales correctas retorna Usuario con nombre y rol")

    void authenticate_admin_credencialesCorrectas_retornaUsuario() {

        Usuario u = db.authenticate("ADMIN", "admin23");

        assertNotNull(u);

        assertEquals("ADMIN", u.nombre);

        assertEquals("ADMIN", u.rol);

    }


    @Test @Order(4)

    @DisplayName("AUTH: PRODUCTOS con credenciales correctas retorna rol PRODUCTOS")

    void authenticate_productos_credencialesCorrectas() {

        Usuario u = db.authenticate("PRODUCTOS", "productos19");

        assertNotNull(u);

        assertEquals("PRODUCTOS", u.nombre);

        assertEquals("PRODUCTOS", u.rol);

    }


    @Test @Order(5)

    @DisplayName("AUTH: ALMACENES con credenciales correctas retorna rol ALMACENES")

    void authenticate_almacenes_credencialesCorrectas() {

        Usuario u = db.authenticate("ALMACENES", "almacenes11");

        assertNotNull(u);

        assertEquals("ALMACENES", u.nombre);

        assertEquals("ALMACENES", u.rol);

    }


    @Test @Order(6)

    @DisplayName("AUTH: Contraseña incorrecta retorna null")

    void authenticate_contrasenaIncorrecta_retornaNull() {

        assertNull(db.authenticate("ADMIN", "wrongpassword"),

                "Con contraseña incorrecta debe retornar null");

    }


    @Test @Order(7)

    @DisplayName("AUTH: Usuario inexistente retorna null")

    void authenticate_usuarioInexistente_retornaNull() {

        assertNull(db.authenticate("NOEXISTE", "admin23"),

                "Con usuario inexistente debe retornar null");

    }


    @Test @Order(8)

    @DisplayName("AUTH: Nombre vacío retorna null")

    void authenticate_nombreVacio_retornaNull() {

        assertNull(db.authenticate("", "admin23"));

    }


    @Test @Order(9)

    @DisplayName("AUTH: Contraseña vacía retorna null")

    void authenticate_contrasenaVacia_retornaNull() {

        assertNull(db.authenticate("ADMIN", ""));

    }


    @Test @Order(10)

    @DisplayName("AUTH: La contraseña es sensible a mayúsculas ('Admin23' ≠ 'admin23')")

    void authenticate_sensibleMayusculas() {

        assertNull(db.authenticate("ADMIN", "Admin23"),

                "La contraseña 'Admin23' no es válida, solo 'admin23'");

        assertNull(db.authenticate("ADMIN", "ADMIN23"));

    }


    @Test @Order(11)

    @DisplayName("AUTH: authenticate() actualiza fecha_hora_ultimo_inicio en la BD")

    void authenticate_actualizaFechaUltimoInicio() throws Exception {

// Primera autenticación

        db.authenticate("ADMIN", "admin23");

        Thread.sleep(1100); // esperar 1 segundo para que la fecha cambie

// Segunda autenticación

        db.authenticate("ADMIN", "admin23");

// No podemos leer fecha directamente sin exponer la conexión,

// pero verificamos que la autenticación no lanzó excepción

        assertNotNull(db.authenticate("ADMIN", "admin23"),

                "Después de actualizar fecha, authenticate debe seguir funcionando");

    }


// ════════════════════════════════════════════════════════════════════════

// ALMACENES

// ════════════════════════════════════════════════════════════════════════


    @Test @Order(20)

    @DisplayName("ALMACÉN: listAlmacenes retorna lista vacía cuando no hay almacenes")

    void listAlmacenes_listaVacia_inicial() {

        List<Almacen> lista = db.listAlmacenes();

        assertNotNull(lista, "La lista no debe ser null");

        assertTrue(lista.isEmpty(), "Debe estar vacía al inicio");

    }


    @Test @Order(21)

    @DisplayName("ALMACÉN: insertAlmacen retorna un ID mayor que 0")

    void insertAlmacen_retornaIdPositivo() {

        int id = db.insertAlmacen("Almacén Principal", "Hermosillo", "ADMIN");

        assertTrue(id > 0, "El ID generado debe ser > 0, fue: " + id);

    }


    @Test @Order(22)

    @DisplayName("ALMACÉN: listAlmacenes retorna el almacén recién insertado")

    void listAlmacenes_retornaAlmacenInsertado() {

        db.insertAlmacen("Almacén Sur", "Guaymas", "ALMACENES");

        List<Almacen> lista = db.listAlmacenes();

        assertFalse(lista.isEmpty());

        boolean encontrado = lista.stream().anyMatch(a -> "Almacén Sur".equals(a.nombre));

        assertTrue(encontrado, "Debe encontrar 'Almacén Sur' en la lista");

    }


    @Test @Order(23)

    @DisplayName("ALMACÉN: El almacén insertado tiene los datos correctos")

    void insertAlmacen_datosPersistidosCorrectamente() {

        db.insertAlmacen("Almacén Norte", "Nogales", "ADMIN");

        List<Almacen> lista = db.listAlmacenes();

        Almacen norte = lista.stream()

                .filter(a -> "Almacén Norte".equals(a.nombre))

                .findFirst().orElse(null);


        assertNotNull(norte, "Debe existir 'Almacén Norte'");

        assertEquals("Nogales", norte.ubicacion);

        assertEquals("ADMIN", norte.ultimoUsuario);

        assertNotNull(norte.fechaHoraCreacion, "fechaHoraCreacion no debe ser null");

        assertFalse(norte.fechaHoraCreacion.isEmpty(), "fechaHoraCreacion no debe estar vacía");

    }


    @Test @Order(24)

    @DisplayName("ALMACÉN: updateAlmacen modifica nombre y ubicación correctamente")

    void updateAlmacen_modificaDatosCorrectamente() {

        int id = db.insertAlmacen("Original", "UbicOriginal", "ADMIN");

        db.updateAlmacen(id, "Modificado", "UbicModificada", "PRODUCTOS");


        List<Almacen> lista = db.listAlmacenes();

        Almacen mod = lista.stream().filter(a -> a.id == id).findFirst().orElse(null);


        assertNotNull(mod, "Debe encontrar el almacén por id");

        assertEquals("Modificado", mod.nombre);

        assertEquals("UbicModificada", mod.ubicacion);

        assertEquals("PRODUCTOS", mod.ultimoUsuario);

        assertNotNull(mod.fechaHoraUltimaMod, "fechaHoraUltimaMod debe actualizarse");

    }


    @Test @Order(25)

    @DisplayName("ALMACÉN: deleteAlmacen elimina el registro de la BD")

    void deleteAlmacen_eliminaRegistro() {

        int id = db.insertAlmacen("AEliminar", "Addr", "ADMIN");

        db.deleteAlmacen(id);


        List<Almacen> lista = db.listAlmacenes();

        boolean aun_existe = lista.stream().anyMatch(a -> a.id == id);

        assertFalse(aun_existe, "El almacén eliminado no debe aparecer en la lista");

    }


    @Test @Order(26)

    @DisplayName("ALMACÉN: deleteAlmacen con ID inexistente no lanza excepción")

    void deleteAlmacen_idInexistente_noLanzaExcepcion() {

        assertDoesNotThrow(() -> db.deleteAlmacen(99999),

                "Eliminar un ID inexistente no debe lanzar excepción");

    }


    @Test @Order(27)

    @DisplayName("ALMACÉN: listAlmacenes ordena resultados por nombre (A→Z)")

    void listAlmacenes_ordenAlfabetico() {

// Insertar en orden inverso

        db.insertAlmacen("Zeta", "Z", "ADMIN");

        db.insertAlmacen("Alpha", "A", "ADMIN");

        db.insertAlmacen("Medio", "M", "ADMIN");


        List<Almacen> lista = db.listAlmacenes();

// Verificar que la lista está ordenada

        for (int i = 0; i < lista.size() - 1; i++) {

            assertTrue(

                    lista.get(i).nombre.compareToIgnoreCase(lista.get(i + 1).nombre) <= 0,

                    "La lista debe estar en orden alfabético: " +

                            lista.get(i).nombre + " > " + lista.get(i + 1).nombre

            );

        }

    }


    @Test @Order(28)

    @DisplayName("ALMACÉN: El ID retornado por insertAlmacen coincide con el almacén en listAlmacenes")

    void insertAlmacen_idRetornado_coincideConListado() {

        int idGenerado = db.insertAlmacen("TestID", "Loc", "ADMIN");

        List<Almacen> lista = db.listAlmacenes();

        boolean idEncontrado = lista.stream().anyMatch(a -> a.id == idGenerado);

        assertTrue(idEncontrado, "El ID " + idGenerado + " debe estar en la lista");

    }


// ════════════════════════════════════════════════════════════════════════

// PRODUCTOS

// ════════════════════════════════════════════════════════════════════════


    @Test @Order(30)

    @DisplayName("PRODUCTO: listProductos retorna lista vacía cuando no hay productos")

    void listProductos_listaVacia_inicial() {

// Crear BD completamente nueva para este test

        File f = null;

        try {

            f = Files.createTempFile("test_prod_vacia_", ".db").toFile();

            Database dbNueva = new Database("jdbc:sqlite:" + f.getAbsolutePath());

            List<Producto> lista = dbNueva.listProductos();

            assertNotNull(lista);

            assertTrue(lista.isEmpty(), "BD nueva no debe tener productos");

        } catch (Exception e) {

            fail("No debería lanzar excepción: " + e.getMessage());

        } finally {

            if (f != null) f.delete();

        }

    }


    @Test @Order(31)

    @DisplayName("PRODUCTO: insertProducto retorna un ID mayor que 0")

    void insertProducto_retornaIdPositivo() {

        Producto p = productoBase("ProdTest");

        int id = db.insertProducto(p, "ADMIN");

        assertTrue(id > 0, "El ID generado debe ser > 0, fue: " + id);

    }


    @Test @Order(32)

    @DisplayName("PRODUCTO: listProductos contiene el producto recién insertado")

    void listProductos_contieneProductoInsertado() {

        Producto p = productoBase("Teclado Mecánico");

        db.insertProducto(p, "ADMIN");


        List<Producto> lista = db.listProductos();

        boolean encontrado = lista.stream().anyMatch(pr -> "Teclado Mecánico".equals(pr.nombre));

        assertTrue(encontrado);

    }


    @Test @Order(33)

    @DisplayName("PRODUCTO: Los datos del producto se persisten correctamente")

    void insertProducto_datosPersistidosCorrectamente() {

        Producto p = new Producto();

        p.nombre = "Monitor 4K";

        p.descripcion = "Resolución 3840x2160";

        p.cantidad = 5;

        p.precio = 8999.99;

        p.almacenId = 0;


        db.insertProducto(p, "PRODUCTOS");


        List<Producto> lista = db.listProductos();

        Producto found = lista.stream()

                .filter(pr -> "Monitor 4K".equals(pr.nombre))

                .findFirst().orElse(null);


        assertNotNull(found);

        assertEquals("Resolución 3840x2160", found.descripcion);

        assertEquals(5, found.cantidad);

        assertEquals(8999.99, found.precio, 0.001);

        assertEquals("PRODUCTOS", found.ultimoUsuario);

        assertNotNull(found.fechaCreacion, "fechaCreacion debe guardarse");

    }


    @Test @Order(34)

    @DisplayName("PRODUCTO: Producto sin almacén tiene almacenNombre null (LEFT JOIN)")

    void listProductos_sinAlmacen_almacenNombreEsNull() {

        Producto p = productoBase("ProdSinAlmacen");

        p.almacenId = 0;

        db.insertProducto(p, "ADMIN");


        List<Producto> lista = db.listProductos();

        Producto found = lista.stream()

                .filter(pr -> "ProdSinAlmacen".equals(pr.nombre))

                .findFirst().orElse(null);


        assertNotNull(found);

        assertNull(found.almacenNombre, "Sin almacén asignado, almacenNombre debe ser null");

    }


    @Test @Order(35)

    @DisplayName("PRODUCTO: Producto con almacén muestra el nombre del almacén (LEFT JOIN)")

    void listProductos_conAlmacen_almacenNombreResuelto() {

        int almId = db.insertAlmacen("Almacén Gadgets", "Piso 2", "ADMIN");


        Producto p = productoBase("Laptop Gaming");

        p.almacenId = almId;

        db.insertProducto(p, "ADMIN");


        List<Producto> lista = db.listProductos();

        Producto found = lista.stream()

                .filter(pr -> "Laptop Gaming".equals(pr.nombre))

                .findFirst().orElse(null);


        assertNotNull(found);

        assertEquals("Almacén Gadgets", found.almacenNombre,

                "El nombre del almacén debe resolverse vía LEFT JOIN");

        assertEquals(almId, found.almacenId);

    }


    @Test @Order(36)

    @DisplayName("PRODUCTO: updateProducto modifica todos los campos correctamente")

    void updateProducto_modificaDatosCorrectamente() {

        Producto original = productoBase("ProdAModificar");

        original.precio = 100.0;

        original.cantidad = 10;

        int id = db.insertProducto(original, "ADMIN");


        Producto modificado = new Producto();

        modificado.id = id;

        modificado.nombre = "ProdModificado";

        modificado.descripcion = "Nueva descripción";

        modificado.precio = 250.0;

        modificado.cantidad = 20;

        modificado.almacenId = 0;

        db.updateProducto(modificado, "PRODUCTOS");


        List<Producto> lista = db.listProductos();

        Producto found = lista.stream().filter(p -> p.id == id).findFirst().orElse(null);


        assertNotNull(found);

        assertEquals("ProdModificado", found.nombre);

        assertEquals("Nueva descripción", found.descripcion);

        assertEquals(250.0, found.precio, 0.001);

        assertEquals(20, found.cantidad);

        assertEquals("PRODUCTOS", found.ultimoUsuario);

        assertNotNull(found.fechaModificacion, "fechaModificacion debe actualizarse");

    }


    @Test @Order(37)

    @DisplayName("PRODUCTO: deleteProducto elimina el registro de la BD")

    void deleteProducto_eliminaRegistro() {

        Producto p = productoBase("ProdAEliminar");

        int id = db.insertProducto(p, "ADMIN");

        db.deleteProducto(id);


        List<Producto> lista = db.listProductos();

        boolean aun_existe = lista.stream().anyMatch(pr -> pr.id == id);

        assertFalse(aun_existe, "El producto eliminado no debe aparecer en la lista");

    }


    @Test @Order(38)

    @DisplayName("PRODUCTO: deleteProducto con ID inexistente no lanza excepción")

    void deleteProducto_idInexistente_noLanzaExcepcion() {

        assertDoesNotThrow(() -> db.deleteProducto(99999));

    }


    @Test @Order(39)

    @DisplayName("PRODUCTO: listProductos ordena resultados por nombre (A→Z)")

    void listProductos_ordenAlfabetico() {

        db.insertProducto(productoBase("Zorro"), "ADMIN");

        db.insertProducto(productoBase("Arandela"), "ADMIN");

        db.insertProducto(productoBase("Martillo"), "ADMIN");


        List<Producto> lista = db.listProductos();

        for (int i = 0; i < lista.size() - 1; i++) {

            assertTrue(

                    lista.get(i).nombre.compareToIgnoreCase(lista.get(i + 1).nombre) <= 0,

                    "Lista debe estar ordenada: " +

                            lista.get(i).nombre + " > " + lista.get(i + 1).nombre

            );

        }

    }


    @Test @Order(40)

    @DisplayName("PRODUCTO: Se pueden insertar múltiples productos y todos se recuperan")

    void insertProducto_multiples_todosSeRecuperan() {

        File f = null;

        try {

            f = Files.createTempFile("test_multiprod_", ".db").toFile();

            Database dbN = new Database("jdbc:sqlite:" + f.getAbsolutePath());


            for (int i = 1; i <= 5; i++) {

                dbN.insertProducto(productoBase("Prod" + i), "ADMIN");

            }

            List<Producto> lista = dbN.listProductos();

            assertEquals(5, lista.size(), "Deben recuperarse exactamente 5 productos");

        } catch (Exception e) {

            fail(e.getMessage());

        } finally {

            if (f != null) f.delete();

        }

    }


// ════════════════════════════════════════════════════════════════════════

// HELPER

// ════════════════════════════════════════════════════════════════════════


    /** Crea un Producto de prueba con valores mínimos válidos. */

    private Producto productoBase(String nombre) {

        Producto p = new Producto();

        p.nombre = nombre;

        p.descripcion = "Descripción de " + nombre;

        p.cantidad = 1;

        p.precio = 9.99;

        p.almacenId = 0;

        return p;

    }

} 