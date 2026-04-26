package mx.unison;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuarios")
public class Usuario {
    
    @DatabaseField(id = true)
    public String nombre;
    
    @DatabaseField
    public String rol;

    // ORMLite requiere un constructor vacío
    public Usuario() {}
}