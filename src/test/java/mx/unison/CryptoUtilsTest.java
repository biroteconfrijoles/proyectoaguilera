package mx.unison;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CryptoUtils.
 *
 * Cubre:
 *  - Correctitud del hash MD5 para valores conocidos
 *  - Formato de salida (32 caracteres hexadecimales en minúsculas)
 *  - Consistencia (misma entrada → misma salida)
 *  - Sensibilidad a mayúsculas/minúsculas
 *  - Manejo de cadena vacía y caracteres especiales/Unicode
 *  - Verificación de que distintas entradas producen distintos hashes
 */
@DisplayName("CryptoUtils – Hash MD5")
class CryptoUtilsTest {

    // ── Correctitud con valores conocidos ────────────────────────────────────

    @Test
    @DisplayName("MD5 de 'admin23' debe ser el hash correcto")
    void md5_admin23_hashCorrecto() {
        // Valor verificado con herramienta externa (md5sum)
        String esperado = "c289ffe12a30c94530b7fc4e532e2f42";
        assertEquals(esperado, CryptoUtils.md5("admin23"),
            "El hash de 'admin23' debe coincidir exactamente");
    }

    @Test
    @DisplayName("MD5 de 'productos19' debe ser el hash correcto")
    void md5_productos19_hashCorrecto() {
        String esperado = "dd378394cfc3b3b98f7d268ccf032201";
        assertEquals(esperado, CryptoUtils.md5("productos19"));
    }

    @Test
    @DisplayName("MD5 de 'almacenes11' debe ser el hash correcto")
    void md5_almacenes11_hashCorrecto() {
        String esperado = "2ef711504785fe694ac41b9986758a57";
        assertEquals(esperado, CryptoUtils.md5("almacenes11"));
    }

    @Test
    @DisplayName("MD5 de cadena vacía debe ser el hash conocido d41d8cd9...")
    void md5_cadenaVacia_hashCorrecto() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", CryptoUtils.md5(""));
    }

    // ── Formato de salida ────────────────────────────────────────────────────

    @Test
    @DisplayName("El hash debe tener exactamente 32 caracteres")
    void md5_longitud32Caracteres() {
        assertEquals(32, CryptoUtils.md5("cualquier texto").length());
        assertEquals(32, CryptoUtils.md5("a").length());
        assertEquals(32, CryptoUtils.md5("").length());
    }

    @Test
    @DisplayName("El hash debe contener solo caracteres hexadecimales en minúsculas")
    void md5_soloHexMinusculas() {
        String hash = CryptoUtils.md5("inventario123");
        assertTrue(hash.matches("[0-9a-f]{32}"),
            "El hash debe ser exactamente 32 hex en minúsculas: " + hash);
    }

    @Test
    @DisplayName("El hash NO debe contener letras mayúsculas")
    void md5_sinMayusculas() {
        String hash = CryptoUtils.md5("TestMayusculas");
        assertEquals(hash, hash.toLowerCase(),
            "md5() debe retornar siempre en minúsculas");
    }

    // ── Consistencia ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Llamadas múltiples con misma entrada deben retornar el mismo hash")
    void md5_consistencia_mismaEntradaMismoResultado() {
        String entrada = "consistencia_test";
        String h1 = CryptoUtils.md5(entrada);
        String h2 = CryptoUtils.md5(entrada);
        String h3 = CryptoUtils.md5(entrada);
        assertEquals(h1, h2, "Primera y segunda llamada deben ser iguales");
        assertEquals(h2, h3, "Segunda y tercera llamada deben ser iguales");
    }

    // ── Sensibilidad a mayúsculas/minúsculas ─────────────────────────────────

    @Test
    @DisplayName("'admin' y 'ADMIN' deben producir hashes distintos")
    void md5_sensibleMayusculas() {
        assertNotEquals(CryptoUtils.md5("admin"), CryptoUtils.md5("ADMIN"),
            "MD5 debe distinguir mayúsculas de minúsculas");
    }

    @Test
    @DisplayName("'admin23' y 'Admin23' deben producir hashes distintos")
    void md5_sensibleMayusculas_credenciales() {
        assertNotEquals(CryptoUtils.md5("admin23"), CryptoUtils.md5("Admin23"));
    }

    // ── Distintas entradas → distintos hashes ────────────────────────────────

    @Test
    @DisplayName("Las tres contraseñas de usuario base deben tener hashes distintos")
    void md5_contraseñasBase_todasDistintas() {
        String h1 = CryptoUtils.md5("admin23");
        String h2 = CryptoUtils.md5("productos19");
        String h3 = CryptoUtils.md5("almacenes11");
        assertNotEquals(h1, h2, "admin23 y productos19 no deben colisionar");
        assertNotEquals(h2, h3, "productos19 y almacenes11 no deben colisionar");
        assertNotEquals(h1, h3, "admin23 y almacenes11 no deben colisionar");
    }

    @Test
    @DisplayName("Entradas con un solo carácter diferente deben producir hashes distintos")
    void md5_pequeñaDiferencia_hashDiferente() {
        assertNotEquals(CryptoUtils.md5("password1"), CryptoUtils.md5("password2"));
        assertNotEquals(CryptoUtils.md5("aaa"), CryptoUtils.md5("aab"));
    }

    // ── Caracteres especiales y Unicode ──────────────────────────────────────

    @Test
    @DisplayName("Debe procesar caracteres con acentos (UTF-8)")
    void md5_caracteresAcentuados_noLanzaExcepcion() {
        assertDoesNotThrow(() -> CryptoUtils.md5("contraseña123"));
        assertDoesNotThrow(() -> CryptoUtils.md5("almacén"));
        assertEquals(32, CryptoUtils.md5("niño").length());
    }

    @Test
    @DisplayName("Debe procesar cadenas largas sin error")
    void md5_cadenaLarga_funcionaCorrectamente() {
        String larga = "a".repeat(10_000);
        String hash = CryptoUtils.md5(larga);
        assertNotNull(hash);
        assertEquals(32, hash.length());
    }

    @Test
    @DisplayName("Espacios en blanco son parte de la entrada y producen hash distinto")
    void md5_espaciosEnBlanco_SonSignificativos() {
        assertNotEquals(CryptoUtils.md5("admin"), CryptoUtils.md5(" admin"));
        assertNotEquals(CryptoUtils.md5("admin"), CryptoUtils.md5("admin "));
        assertNotEquals(CryptoUtils.md5("a b"), CryptoUtils.md5("ab"));
    }

    // ── Null (comportamiento esperado: RuntimeException) ─────────────────────

    @Test
    @DisplayName("Pasar null debe lanzar RuntimeException")
    void md5_null_lanzaRuntimeException() {
        assertThrows(RuntimeException.class, () -> CryptoUtils.md5(null),
            "md5(null) debe propagar una excepción en tiempo de ejecución");
    }
}
