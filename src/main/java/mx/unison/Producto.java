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
}