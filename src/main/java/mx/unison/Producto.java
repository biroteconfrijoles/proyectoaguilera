package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "productos")
public class Producto {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String nombre;

    @DatabaseField
    public String descripcion;

    @DatabaseField
    public int cantidad;

    @DatabaseField
    public double precio;

    @DatabaseField
    public int almacenId;

    @DatabaseField
    public String almacenNombre;

    @DatabaseField
    public String fechaCreacion;

    @DatabaseField
    public String fechaModificacion;

    @DatabaseField
    public String ultimoUsuario;

    public Producto() {}

    // ── Getters requeridos por PropertyValueFactory ──
    public int    getId()               { return id; }
    public String getNombre()           { return nombre; }
    public String getDescripcion()      { return descripcion; }
    public int    getCantidad()         { return cantidad; }
    public double getPrecio()           { return precio; }
    public int    getAlmacenId()        { return almacenId; }
    public String getAlmacenNombre()    { return almacenNombre; }
    public String getFechaCreacion()    { return fechaCreacion; }
    public String getFechaModificacion(){ return fechaModificacion; }
    public String getUltimoUsuario()    { return ultimoUsuario; }
}
