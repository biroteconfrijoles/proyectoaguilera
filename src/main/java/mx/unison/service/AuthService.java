package mx.unison.service;

import mx.unison.Database;
import mx.unison.Usuario;

/**
 * Servicio de autenticación.
 * Encapsula toda la lógica relacionada con el login y la sesión activa.
 * El controlador solo llama a este servicio; no conoce la BD directamente.
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see LoginController
 */
public class AuthService {

    /** Conexión a la base de datos */
    private final Database db;
    
    /** Usuario actualmente autenticado, o null si no hay sesión */
    private Usuario usuarioActual;

    /**
     * Constructor que inicializa el servicio de autenticación.
     * 
     * @param db instancia de la base de datos
     */
    public AuthService(Database db) {
        this.db = db;
    }

    /**
     * Intenta autenticar con las credenciales dadas.
     * 
     * @param nombre nombre de usuario
     * @param password contraseña en texto plano
     * @return true si el login fue exitoso, false en caso contrario
     */
    public boolean login(String nombre, String password) {
        if (nombre == null || nombre.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        Usuario u = db.authenticate(nombre.trim().toUpperCase(), password);
        if (u != null) {
            usuarioActual = u;
            return true;
        }
        return false;
    }

    /** Cierra la sesión actual. */
    public void logout() {
        usuarioActual = null;
    }

    /** Devuelve el usuario autenticado, o null si no hay sesión. */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** Verifica si hay una sesión activa. */
    public boolean haySesion() {
        return usuarioActual != null;
    }

    // ── Helpers de rol ───────────────────────────────────────────────────────

    public boolean esAdmin() {
        return haySesion() && "ADMIN".equals(usuarioActual.rol);
    }

    public boolean puedeGestionarProductos() {
        return haySesion() && (esAdmin() || "PRODUCTOS".equals(usuarioActual.rol));
    }

    public boolean puedeGestionarAlmacenes() {
        return haySesion() && (esAdmin() || "ALMACENES".equals(usuarioActual.rol));
    }

    public String getNombreUsuario() {
        return haySesion() ? usuarioActual.nombre : "Sistema";
    }
}
