package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Entidad que representa un Producto en el inventario.
 * Mapeo ORM con la tabla "productos" en la base de datos SQLite.
 * 
 * <p>Atributos principales:
 * <ul>
 *   <li>id: Identificador único del producto (generado automáticamente)</li>
 *   <li>nombre: Nombre descriptivo del producto</li>
 *   <li>descripcion: Descripción detallada del producto</li>
 *   <li>cantidad: Cantidad disponible en inventario</li>
 *   <li>precio: Precio unitario del producto</li>
 *   <li>almacenId: ID del almacén donde se encuentra</li>
 * </ul>
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see ProductoService
 */
@DatabaseTable(tableName = "productos")
public class Producto {

    /** Identificador único del producto, generado automáticamente */
    @DatabaseField(generatedId = true)
    public int id;

    /** Nombre del producto */
    @DatabaseField
    public String nombre;

    /** Descripción detallada del producto */
    @DatabaseField
    public String descripcion;

    /** Cantidad de unidades en inventario */
    @DatabaseField
    public int cantidad;

    /** Precio unitario del producto */
    @DatabaseField
    public double precio;

    /** ID del almacén donde se encuentra el producto */
    @DatabaseField
    public int almacenId;

    /** Nombre del almacén (para referencia) */
    @DatabaseField
    public String almacenNombre;

    /** Fecha y hora de creación del registro */
    @DatabaseField
    public String fechaCreacion;

    /** Fecha y hora de la última modificación */
    @DatabaseField
    public String fechaModificacion;

    /** Nombre del usuario que realizó la última modificación */
    @DatabaseField
    public String ultimoUsuario;

    /**
     * Constructor por defecto requerido por ORMLite.
     */
    public Producto() {}

    // ── Getters requeridos por PropertyValueFactory ──
    
    /**
     * Obtiene el ID del producto.
     * @return id del producto
     */
    public int    getId()               { return id; }
    
    /**
     * Obtiene el nombre del producto.
     * @return nombre del producto
     */
    public String getNombre()           { return nombre; }
    
    /**
     * Obtiene la descripción del producto.
     * @return descripción del producto
     */
    public String getDescripcion()      { return descripcion; }
    
    /**
     * Obtiene la cantidad en inventario.
     * @return cantidad disponible
     */
    public int    getCantidad()         { return cantidad; }
    
    /**
     * Obtiene el precio unitario.
     * @return precio del producto
     */
    public double getPrecio()           { return precio; }
    
    /**
     * Obtiene el ID del almacén.
     * @return ID del almacén
     */
    public int    getAlmacenId()        { return almacenId; }
    
    /**
     * Obtiene el nombre del almacén.
     * @return nombre del almacén
     */
    public String getAlmacenNombre()    { return almacenNombre; }
    
    /**
     * Obtiene la fecha de creación.
     * @return fecha de creación del registro
     */
    public String getFechaCreacion()    { return fechaCreacion; }
    
    /**
     * Obtiene la fecha de última modificación.
     * @return fecha de última modificación
     */
    public String getFechaModificacion(){ return fechaModificacion; }
    public String getUltimoUsuario()    { return ultimoUsuario; }
}
