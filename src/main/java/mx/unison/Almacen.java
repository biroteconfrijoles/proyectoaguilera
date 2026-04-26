package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "almacenes")
public class Almacen {
    
    @DatabaseField(generatedId = true)
    public int id;
    
    @DatabaseField
    public String nombre;
    
    @DatabaseField
    public String ubicacion;
    
    @DatabaseField
    public String fechaHoraCreacion;
    
    @DatabaseField
    public String fechaHoraUltimaMod;
    
    @DatabaseField
    public String ultimoUsuario;

    public Almacen() {}
}