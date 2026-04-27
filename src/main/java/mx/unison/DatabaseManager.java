package mx.unison;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Gestor de la base de datos SQLite usando ORMLite.
 * Proporciona acceso a los DAOs para las entidades principales:
 * Usuario, Producto y Almacén.
 * 
 * <p>ORMLite es un framework ORM ligero que simplifica la mapeo de
 * objetos a la base de datos relacional.
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see Usuario
 * @see Producto
 * @see Almacen
 */
public class DatabaseManager {
    /** URL de conexión a la base de datos SQLite */
    private static final String DATABASE_URL = "jdbc:sqlite:InventarioBD.db";
    
    /** Fuente de conexión a la base de datos */
    private ConnectionSource connectionSource;
    
    /** DAO para acceso a usuarios */
    private Dao<Usuario, String> usuarioDao;
    
    /** DAO para acceso a productos */
    private Dao<Producto, Integer> productoDao;
    
    /** DAO para acceso a almacenes */
    private Dao<Almacen, Integer> almacenDao;

    /**
     * Constructor que inicializa la conexión y crea las tablas si no existen.
     * 
     * @throws SQLException si hay error en la conexión a la base de datos
     */
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

    /**
     * Obtiene el DAO para acceso a usuarios.
     * 
     * @return DAO de usuarios
     */
    public Dao<Usuario, String> getUsuarioDao() { 
        return usuarioDao; 
    }
    
    /**
     * Obtiene el DAO para acceso a productos.
     * 
     * @return DAO de productos
     */
    public Dao<Producto, Integer> getProductoDao() { 
        return productoDao; 
    }
    
    /**
     * Obtiene el DAO para acceso a almacenes.
     * 
     * @return DAO de almacenes
     */
    public Dao<Almacen, Integer> getAlmacenDao() { 
        return almacenDao; 
    }
    
    /**
     * Cierra la conexión con la base de datos.
     * Debe llamarse al finalizar la aplicación.
     * 
     * @throws Exception si hay error al cerrar la conexión
     */
    public void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }
}
