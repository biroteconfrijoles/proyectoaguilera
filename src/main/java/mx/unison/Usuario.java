package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Entidad que representa un Usuario del sistema de inventario.
 * Mapeo ORM con la tabla "usuarios" en la base de datos SQLite.
 * 
 * <p>Los usuarios tienen permisos basados en roles:
 * <ul>
 *   <li>ADMIN: Acceso completo al sistema</li>
 *   <li>PRODUCTOS: Gestión de productos</li>
 *   <li>ALMACENES: Gestión de almacenes</li>
 * </ul>
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see AuthService
 */
@DatabaseTable(tableName = "usuarios")
public class Usuario {

    /** Nombre de usuario único, clave primaria */
    @DatabaseField(id = true)
    public String nombre;

    /** Rol del usuario (ADMIN, PRODUCTOS, ALMACENES) */
    @DatabaseField
    public String rol;

    /**
     * Constructor por defecto requerido por ORMLite.
     */
    public Usuario() {}

    // ── Getters requeridos por PropertyValueFactory ──
    
    /**
     * Obtiene el nombre del usuario.
     * @return nombre de usuario
     */
    public String getNombre() { return nombre; }
    
    /**
     * Obtiene el rol del usuario.
     * @return rol del usuario
     */
    public String getRol()    { return rol; }
}
