//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mx.unison;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Producto – Clase modelo")
class ProductoTest {
    @Test
    @DisplayName("Se puede crear una instancia de Producto sin argumentos")
    void constructor_creaInstancia() {
        Assertions.assertDoesNotThrow(() -> new Producto());
    }

    @Test
    @DisplayName("Los campos int y double son 0 por defecto")
    void camposNumericos_defecto_sonCero() {
        Producto p = new Producto();
        Assertions.assertEquals(0, p.id, "id debe ser 0");
        Assertions.assertEquals(0, p.cantidad, "cantidad debe ser 0");
        Assertions.assertEquals(0, p.almacenId, "almacenId debe ser 0");
        Assertions.assertEquals((double)0.0F, p.precio, "precio debe ser 0.0");
    }

    @Test
    @DisplayName("Los campos String son null por defecto")
    void camposString_defecto_sonNull() {
        Producto p = new Producto();
        Assertions.assertNull(p.nombre, "nombre debe ser null");
        Assertions.assertNull(p.descripcion, "descripcion debe ser null");
        Assertions.assertNull(p.almacenNombre, "almacenNombre debe ser null");
        Assertions.assertNull(p.fechaCreacion, "fechaCreacion debe ser null");
        Assertions.assertNull(p.fechaModificacion, "fechaModificacion debe ser null");
        Assertions.assertNull(p.ultimoUsuario, "ultimoUsuario debe ser null");
    }

    @Test
    @DisplayName("Se pueden asignar y leer todos los campos correctamente")
    void todosLosCampos_asignacionYLectura() {
        Producto p = new Producto();
        p.id = 10;
        p.nombre = "Caja de Herramientas";
        p.descripcion = "Kit profesional 50 piezas";
        p.cantidad = 25;
        p.precio = 599.99;
        p.almacenId = 3;
        p.almacenNombre = "Almacén Sur";
        p.fechaCreacion = "2026-01-15 09:00:00";
        p.fechaModificacion = "2026-03-10 14:30:00";
        p.ultimoUsuario = "PRODUCTOS";
        Assertions.assertEquals(10, p.id);
        Assertions.assertEquals("Caja de Herramientas", p.nombre);
        Assertions.assertEquals("Kit profesional 50 piezas", p.descripcion);
        Assertions.assertEquals(25, p.cantidad);
        Assertions.assertEquals(599.99, p.precio, 0.001);
        Assertions.assertEquals(3, p.almacenId);
        Assertions.assertEquals("Almacén Sur", p.almacenNombre);
        Assertions.assertEquals("2026-01-15 09:00:00", p.fechaCreacion);
        Assertions.assertEquals("2026-03-10 14:30:00", p.fechaModificacion);
        Assertions.assertEquals("PRODUCTOS", p.ultimoUsuario);
    }

    @Test
    @DisplayName("El precio puede ser 0.0 (producto gratuito)")
    void precio_puedeSerCero() {
        Producto p = new Producto();
        p.precio = (double)0.0F;
        Assertions.assertEquals((double)0.0F, p.precio, 1.0E-4);
    }

    @Test
    @DisplayName("El precio puede ser un decimal con dos decimales")
    void precio_dosDecimales() {
        Producto p = new Producto();
        p.precio = 1234.56;
        Assertions.assertEquals(1234.56, p.precio, 0.001);
    }

    @Test
    @DisplayName("El precio puede ser muy alto (sin límite en el modelo)")
    void precio_valorAlto() {
        Producto p = new Producto();
        p.precio = 999999.99;
        Assertions.assertEquals(999999.99, p.precio, 0.001);
    }

    @Test
    @DisplayName("El precio puede ser negativo (modelo no valida, validación está en la UI)")
    void precio_negativo_aceptadoPorModelo() {
        Producto p = new Producto();
        p.precio = (double)-50.0F;
        Assertions.assertEquals((double)-50.0F, p.precio, 0.001, "El modelo no restringe precios negativos; la UI debe validarlo");
    }

    @Test
    @DisplayName("La cantidad puede ser 0 (sin stock)")
    void cantidad_puedeSerCero() {
        Producto p = new Producto();
        p.cantidad = 0;
        Assertions.assertEquals(0, p.cantidad);
    }

    @Test
    @DisplayName("La cantidad puede ser negativa (modelo no valida, validación está en la UI)")
    void cantidad_negativa_aceptadaPorModelo() {
        Producto p = new Producto();
        p.cantidad = -5;
        Assertions.assertEquals(-5, p.cantidad, "El modelo no restringe cantidades negativas; la UI debe validarlo");
    }

    @Test
    @DisplayName("La cantidad puede ser un número grande")
    void cantidad_valorGrande() {
        Producto p = new Producto();
        p.cantidad = Integer.MAX_VALUE;
        Assertions.assertEquals(Integer.MAX_VALUE, p.cantidad);
    }

    @Test
    @DisplayName("almacenId=0 representa 'sin almacén asignado'")
    void almacenId_cero_sinAlmacen() {
        Producto p = new Producto();
        p.almacenId = 0;
        Assertions.assertEquals(0, p.almacenId);
    }

    @Test
    @DisplayName("almacenNombre puede ser null cuando el producto no tiene almacén")
    void almacenNombre_puedeSerNull() {
        Producto p = new Producto();
        p.almacenId = 0;
        p.almacenNombre = null;
        Assertions.assertEquals(0, p.almacenId);
        Assertions.assertNull(p.almacenNombre);
    }

    @Test
    @DisplayName("almacenId y almacenNombre son independientes entre sí en el modelo")
    void almacenId_y_almacenNombre_sonIndependientes() {
        Producto p = new Producto();
        p.almacenId = 5;
        p.almacenNombre = "Almacén Principal";
        Assertions.assertEquals(5, p.almacenId);
        Assertions.assertEquals("Almacén Principal", p.almacenNombre);
    }

    @Test
    @DisplayName("Dos instancias de Producto son independientes")
    void dosInstancias_sonIndependientes() {
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        p1.nombre = "Producto A";
        p1.precio = (double)10.0F;
        p2.nombre = "Producto B";
        p2.precio = (double)20.0F;
        Assertions.assertEquals("Producto A", p1.nombre);
        Assertions.assertEquals("Producto B", p2.nombre);
        Assertions.assertNotEquals(p1.precio, p2.precio);
        Assertions.assertNotSame(p1, p2);
    }
}
