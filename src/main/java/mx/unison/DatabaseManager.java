package mx.unison;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    // Ruta a tu base de datos SQLite actual
    private static final String DATABASE_URL = "jdbc:sqlite:InventarioBD.db";
    
    private ConnectionSource connectionSource;
    
    // Declaración de los DAOs proporcionados por ORMLite
    private Dao<Usuario, String> usuarioDao;
    private Dao<Producto, Integer> productoDao;
    private Dao<Almacen, Integer> almacenDao;

    public DatabaseManager() throws SQLException {
        // Inicializar conexión
        connectionSource = new JdbcConnectionSource(DATABASE_URL);
        
        // Crear las tablas en la BD si no existen
        TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
        TableUtils.createTableIfNotExists(connectionSource, Producto.class);
        TableUtils.createTableIfNotExists(connectionSource, Almacen.class);
        
        // Inicializar los DAOs
        usuarioDao = DaoManager.createDao(connectionSource, Usuario.class);
        productoDao = DaoManager.createDao(connectionSource, Producto.class);
        almacenDao = DaoManager.createDao(connectionSource, Almacen.class);
    }

    // Getters para acceder a los DAOs desde otras partes de la app
    public Dao<Usuario, String> getUsuarioDao() { 
        return usuarioDao; 
    }
    
    public Dao<Producto, Integer> getProductoDao() { 
        return productoDao; 
    }
    
    public Dao<Almacen, Integer> getAlmacenDao() { 
        return almacenDao; 
    }
    
    // Importante cerrar la conexión al terminar
    public void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }
}
