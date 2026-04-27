package mx.unison;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Utilidades de criptografía para el sistema de inventario.
 * Proporciona funciones para encriptación de contraseñas usando MD5.
 * 
 * <p><b>Nota de seguridad:</b> MD5 no es recomendado para contraseñas en producción.
 * Considere usar bcrypt, PBKDF2 o Argon2 para aplicaciones más seguras.
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 */
public class CryptoUtils {
    /**
     * Calcula el hash MD5 de una cadena de texto.
     * 
     * @param input texto a encriptar
     * @return hash MD5 en formato hexadecimal
     * @throws RuntimeException si el algoritmo MD5 no está disponible o si input es null
     */
    public static String md5(String input) {
        if (input == null) {
            throw new RuntimeException("El texto no puede ser null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar MD5", e);
        }
    }
}