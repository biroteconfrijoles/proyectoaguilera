package mx.unison;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos (DAO) para la base de datos SQLite del sistema de inventario.
 * Gestiona todas las operaciones CRUD para usuarios, almacenes y productos.
 * Utiliza JDBC con sentencias preparadas para evitar inyección SQL.
 * 
 * <p>Las operaciones incluyen:
 * <ul>
 *   <li>Autenticación de usuarios</li>
 *   <li>Gestión completa de almacenes (CREATE, READ, UPDATE, DELETE)</li>
 *   <li>Gestión completa de productos (CREATE, READ, UPDATE, DELETE)</li>
 * </ul>
 * </p>
 * 
 * @author Sistema de Inventario Unison
 * @version 1.0-SNAPSHOT
 * @see Usuario
 * @see Almacen
 * @see Producto
 */
public class Database {
    /** URL de conexión a la base de datos */
    private final String url;

    /**
     * Constructor que inicializa la base de datos.
     * 
     * @param url URL de conexión a la base de datos SQLite
     */
    public Database(String url) {
        this.url = url;
        init();
    }

    /**
     * Establece una nueva conexión con la base de datos.
     * 
     * @return Connection una nueva conexión activa
     * @throws SQLException si hay error en la conexión
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Inicializa la base de datos creando las tablas necesarias
     * e insertando los usuarios base del sistema.
     */
    private void init() {
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (nombre TEXT PRIMARY KEY, password TEXT NOT NULL, rol TEXT NOT NULL, fecha_hora_ultimo_inicio TEXT)";
        String sqlAlmacenes = "CREATE TABLE IF NOT EXISTS almacenes (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, ubicacion TEXT, fecha_hora_creacion TEXT DEFAULT (datetime('now', 'localtime')), fecha_hora_ultima_modificacion TEXT DEFAULT (datetime('now', 'localtime')), ultimo_usuario_en_modificar TEXT)";
        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, descripcion TEXT, cantidad INTEGER DEFAULT 0, precio REAL DEFAULT 0.0, almacen_id INTEGER, fecha_hora_creacion TEXT DEFAULT (datetime('now', 'localtime')), fecha_hora_ultima_modificacion TEXT DEFAULT (datetime('now', 'localtime')), ultimo_usuario_en_modificar TEXT)";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlAlmacenes);
            stmt.execute(sqlProductos);

            insertBaseUser(conn, "ADMIN", "admin23", "ADMIN");
            insertBaseUser(conn, "PRODUCTOS", "productos19", "PRODUCTOS");
            insertBaseUser(conn, "ALMACENES", "almacenes11", "ALMACENES");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserta un usuario base en la base de datos (operación idempotente).
     * 
     * @param conn conexión activa a la base de datos
     * @param nombre nombre de usuario
     * @param pass contraseña en texto plano
     * @param rol rol del usuario (ADMIN, PRODUCTOS, ALMACENES)
     * @throws SQLException si hay error en la inserción
     */
    private void insertBaseUser(Connection conn, String nombre, String pass, String rol) throws SQLException {
        String sql = "INSERT OR IGNORE INTO usuarios (nombre, password, rol) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, CryptoUtils.md5(pass));
            pstmt.setString(3, rol);
            pstmt.executeUpdate();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTENTICACIÓN
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Autentica un usuario validando su nombre y contraseña.
     * Registra la hora del último inicio del usuario.
     * 
     * @param nombre nombre de usuario
     * @param pass contraseña en texto plano
     * @return objeto Usuario con datos autenticados, o null si las credenciales son inválidas
     */
    public Usuario authenticate(String nombre, String pass) {
        if (nombre == null || nombre.isEmpty() || pass == null || pass.isEmpty()) {
            return null;
        }

        String sql = "SELECT rol FROM usuarios WHERE nombre = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, CryptoUtils.md5(pass));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("rol");
                String sqlUpdate = "UPDATE usuarios SET fecha_hora_ultimo_inicio = datetime('now', 'localtime') WHERE nombre = ?";
                try (PreparedStatement update = conn.prepareStatement(sqlUpdate)) {
                    update.setString(1, nombre);
                    update.executeUpdate();
                }

                Usuario u = new Usuario();
                u.nombre = nombre;
                u.rol = rol;
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ALMACENES
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Inserta un nuevo almacén en la base de datos.
     * 
     * @param nombre nombre del almacén
     * @param ubicacion ubicación o dirección del almacén
     * @param usuario usuario que crea el almacén
     * @return ID del almacén creado, o 0 si falla la inserción
     */
    public int insertAlmacen(String nombre, String ubicacion, String usuario) {
        String sql = "INSERT INTO almacenes (nombre, ubicacion, ultimo_usuario_en_modificar) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, ubicacion);
            pstmt.setString(3, usuario);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Actualiza un almacén existente.
     * 
     * @param id ID del almacén a actualizar
     * @param nombre nuevo nombre del almacén
     * @param ubicacion nueva ubicación del almacén
     * @param usuario usuario que realiza la modificación
     */
    public void updateAlmacen(int id, String nombre, String ubicacion, String usuario) {
        String sql = "UPDATE almacenes SET nombre = ?, ubicacion = ?, ultimo_usuario_en_modificar = ?, fecha_hora_ultima_modificacion = datetime('now', 'localtime') WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, ubicacion);
            pstmt.setString(3, usuario);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Elimina un almacén de la base de datos.
     * 
     * @param id ID del almacén a eliminar
     */
    public void deleteAlmacen(int id) {
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM almacenes WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Recupera la lista completa de almacenes ordenada alfabéticamente.
     * 
     * @return lista de almacenes
     */
    public List<Almacen> listAlmacenes() {
        List<Almacen> lista = new ArrayList<>();
        String sql = "SELECT * FROM almacenes ORDER BY nombre COLLATE NOCASE ASC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Almacen a = new Almacen();
                a.id = rs.getInt("id");
                a.nombre = rs.getString("nombre");
                a.ubicacion = rs.getString("ubicacion");
                a.fechaHoraCreacion = rs.getString("fecha_hora_creacion");
                a.fechaHoraUltimaMod = rs.getString("fecha_hora_ultima_modificacion");
                a.ultimoUsuario = rs.getString("ultimo_usuario_en_modificar");
                lista.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PRODUCTOS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Inserta un nuevo producto en la base de datos.
     * Si almacenId es 0, se inserta NULL en la base de datos.
     * 
     * @param p objeto Producto con los datos a insertar
     * @param usuario usuario que crea el producto
     * @return ID del producto creado, o 0 si falla la inserción
     */
    public int insertProducto(Producto p, String usuario) {
        String sql = "INSERT INTO productos (nombre, descripcion, cantidad, precio, almacen_id, ultimo_usuario_en_modificar) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.nombre);
            pstmt.setString(2, p.descripcion);
            pstmt.setInt(3, p.cantidad);
            pstmt.setDouble(4, p.precio);

            if (p.almacenId == 0) {
                pstmt.setNull(5, Types.INTEGER);
            } else {
                pstmt.setInt(5, p.almacenId);
            }

            pstmt.setString(6, usuario);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * 
     * @param p objeto Producto con los datos actualizados
     * @param usuario usuario que realiza la modificación
     */
    public void updateProducto(Producto p, String usuario) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, cantidad = ?, precio = ?, almacen_id = ?, ultimo_usuario_en_modificar = ?, fecha_hora_ultima_modificacion = datetime('now', 'localtime') WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.nombre);
            pstmt.setString(2, p.descripcion);
            pstmt.setInt(3, p.cantidad);
            pstmt.setDouble(4, p.precio);

            if (p.almacenId == 0) {
                pstmt.setNull(5, Types.INTEGER);
            } else {
                pstmt.setInt(5, p.almacenId);
            }

            pstmt.setString(6, usuario);
            pstmt.setInt(7, p.id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Elimina un producto de la base de datos.
     * 
     * @param id ID del producto a eliminar
     */
    public void deleteProducto(int id) {
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Recupera la lista completa de productos ordenada alfabéticamente.
     * Realiza un LEFT JOIN con almacenes para obtener el nombre del almacén.
     * 
     * @return lista de productos con información del almacén asociado
     */
    public List<Producto> listProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, a.nombre AS alm_nom FROM productos p LEFT JOIN almacenes a ON p.almacen_id = a.id ORDER BY p.nombre COLLATE NOCASE ASC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto();
                p.id = rs.getInt("id");
                p.nombre = rs.getString("nombre");
                p.descripcion = rs.getString("descripcion");
                p.cantidad = rs.getInt("cantidad");
                p.precio = rs.getDouble("precio");

                int aId = rs.getInt("almacen_id");
                p.almacenId = rs.wasNull() ? 0 : aId;

                p.almacenNombre = rs.getString("alm_nom");
                p.fechaCreacion = rs.getString("fecha_hora_creacion");
                p.fechaModificacion = rs.getString("fecha_hora_ultima_modificacion");
                p.ultimoUsuario = rs.getString("ultimo_usuario_en_modificar");
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
