package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Entidad que representa un Almacén en el sistema de inventario.
 * Mapeo ORM con la tabla "almacenes" en la base de datos SQLite.
 * 
 * <p>Los almacenes son ubicaciones donde se almacenan los productos.
 * Cada producto pertenece a un almacén identificado por su ID.
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see AlmacenService
 */
@DatabaseTable(tableName = "almacenes")
public class Almacen {

    /** Identificador único del almacén, generado automáticamente */
    @DatabaseField(generatedId = true)
    public int id;

    /** Nombre del almacén */
    @DatabaseField
    public String nombre;

    /** Ubicación o dirección del almacén */
    @DatabaseField
    public String ubicacion;

    /** Fecha y hora de creación del almacén */
    @DatabaseField
    public String fechaHoraCreacion;

    /** Fecha y hora de la última modificación */
    @DatabaseField
    public String fechaHoraUltimaMod;

    /** Usuario que realizó la última modificación */
    @DatabaseField
    public String ultimoUsuario;

    /**
     * Constructor por defecto requerido por ORMLite.
     */
    public Almacen() {}

    // ── Getters requeridos por PropertyValueFactory ──
    
    /**
     * Obtiene el ID del almacén.
     * @return ID del almacén
     */
    public int    getId()                { return id; }
    
    /**
     * Obtiene el nombre del almacén.
     * @return nombre del almacén
     */
    public String getNombre()            { return nombre; }
    
    /**
     * Obtiene la ubicación del almacén.
     * @return ubicación del almacén
     */
    public String getUbicacion()         { return ubicacion; }
    
    /**
     * Obtiene la fecha de creación.
     * @return fecha de creación del almacén
     */
    public String getFechaHoraCreacion() { return fechaHoraCreacion; }
    
    /**
     * Obtiene la fecha de última modificación.
     * @return fecha de última modificación
     */
    public String getFechaHoraUltimaMod(){ return fechaHoraUltimaMod; }
    
    /**
     * Obtiene el usuario que modificó el almacén.
     * @return nombre del usuario que realizó la última modificación
     */
    public String getUltimoUsuario()     { return ultimoUsuario; }
}
