package mx.unison;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class MainController {

    // ========== INYECCIONES FXML ==========
    @FXML private VBox productosView;
    @FXML private VBox almacenesView;
    @FXML private VBox usuariosView;

    @FXML private Button btnProductos;
    @FXML private Button btnAlmacenes;
    @FXML private Button btnUsuarios;

    // Columnas tabla productos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colProdId;
    @FXML private TableColumn<Producto, String>  colProdNombre;
    @FXML private TableColumn<Producto, String>  colProdDesc;
    @FXML private TableColumn<Producto, Integer> colProdCantidad;
    @FXML private TableColumn<Producto, Double>  colProdPrecio;
    @FXML private TableColumn<Producto, String>  colProdAlmacen;
    @FXML private TableColumn<Producto, String>  colProdUsuario;

    // Columnas tabla almacenes
    @FXML private TableView<Almacen> tablaAlmacenes;
    @FXML private TableColumn<Almacen, Integer> colAlmId;
    @FXML private TableColumn<Almacen, String>  colAlmNombre;
    @FXML private TableColumn<Almacen, String>  colAlmUbicacion;
    @FXML private TableColumn<Almacen, String>  colAlmUsuario;

    // Columnas tabla usuarios
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuNombre;
    @FXML private TableColumn<Usuario, String> colUsuRol;

    // Búsqueda
    @FXML private TextField txtSearchProductos;
    @FXML private TextField txtSearchAlmacenes;
    @FXML private TextField txtSearchUsuarios;

    // Campos nuevo producto
    @FXML private TextField prodNombre;
    @FXML private TextField prodDesc;
    @FXML private TextField prodCantidad;
    @FXML private TextField prodPrecio;
    @FXML private TextField prodAlmacen;

    // Campos nuevo almacén
    @FXML private TextField almNombre;
    @FXML private TextField almUbicacion;

    // ========== VARIABLES ==========
    private DatabaseManager dbManager;
    private Database db;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        try {
            dbManager = new DatabaseManager();
            db = new Database("jdbc:sqlite:InventarioBD.db");
            configurarColumnas();
        } catch (SQLException e) {
            mostrarError("Error al inicializar base de datos: " + e.getMessage());
        }
    }

    private void configurarColumnas() {
        // Productos
        if (colProdId       != null) colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colProdNombre   != null) colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colProdDesc     != null) colProdDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        if (colProdCantidad != null) colProdCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        if (colProdPrecio   != null) colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        if (colProdAlmacen  != null) colProdAlmacen.setCellValueFactory(new PropertyValueFactory<>("almacenNombre"));
        if (colProdUsuario  != null) colProdUsuario.setCellValueFactory(new PropertyValueFactory<>("ultimoUsuario"));

        // Almacenes
        if (colAlmId        != null) colAlmId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colAlmNombre    != null) colAlmNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colAlmUbicacion != null) colAlmUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        if (colAlmUsuario   != null) colAlmUsuario.setCellValueFactory(new PropertyValueFactory<>("ultimoUsuario"));

        // Usuarios
        if (colUsuNombre != null) colUsuNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colUsuRol    != null) colUsuRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    /** Llamado por LoginController tras autenticar exitosamente. */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        configurarPermisosPorRol();
        try {
            cargarProductos();
            cargarAlmacenes();
            if ("ADMIN".equals(usuario.rol)) {
                cargarUsuarios();
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
        if ("ALMACENES".equals(usuario.rol)) {
            showAlmacenes();
        } else {
            showProductos();
        }
    }

    private void configurarPermisosPorRol() {
        if (usuarioActual == null) return;
        switch (usuarioActual.rol) {
            case "PRODUCTOS":
                btnAlmacenes.setDisable(true);
                btnUsuarios.setDisable(true);
                break;
            case "ALMACENES":
                btnProductos.setDisable(true);
                btnUsuarios.setDisable(true);
                break;
            default: // ADMIN: acceso total
                break;
        }
    }

    // ========== NAVEGACIÓN ==========
    @FXML private void showProductos() { mostrarVista(productosView); actualizarBotonActivo(btnProductos); }
    @FXML private void showAlmacenes() { mostrarVista(almacenesView); actualizarBotonActivo(btnAlmacenes); }
    @FXML private void showUsuarios()  { mostrarVista(usuariosView);  actualizarBotonActivo(btnUsuarios);  }

    private void mostrarVista(VBox vista) {
        productosView.setVisible(false); productosView.setManaged(false);
        almacenesView.setVisible(false); almacenesView.setManaged(false);
        usuariosView.setVisible(false);  usuariosView.setManaged(false);
        vista.setVisible(true);          vista.setManaged(true);
    }

    private void actualizarBotonActivo(Button activo) {
        btnProductos.getStyleClass().remove("nav-button-active");
        btnAlmacenes.getStyleClass().remove("nav-button-active");
        btnUsuarios.getStyleClass().remove("nav-button-active");
        activo.getStyleClass().add("nav-button-active");
    }

    // ========== CARGAR DATOS ==========
    // Usamos Database (JDBC directo) para el LEFT JOIN que resuelve almacenNombre
    @FXML
    private void cargarProductos() throws SQLException {
        List<Producto> productos = db.listProductos();
        tablaProductos.setItems(FXCollections.observableArrayList(productos));
    }

    @FXML
    private void cargarAlmacenes() throws SQLException {
        List<Almacen> almacenes = db.listAlmacenes();
        tablaAlmacenes.setItems(FXCollections.observableArrayList(almacenes));
    }

    @FXML
    private void cargarUsuarios() throws SQLException {
        List<Usuario> usuarios = dbManager.getUsuarioDao().queryForAll();
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML private void refreshProductos() {
        try { cargarProductos(); } catch (SQLException e) { mostrarError(e.getMessage()); }
    }
    @FXML private void refreshAlmacenes() {
        try { cargarAlmacenes(); } catch (SQLException e) { mostrarError(e.getMessage()); }
    }
    @FXML private void refreshUsuarios() {
        try { cargarUsuarios(); } catch (SQLException e) { mostrarError(e.getMessage()); }
    }

    // ========== CRUD PRODUCTOS ==========
    @FXML
    private void agregarProducto() {
        try {
            String nombre      = prodNombre.getText().trim();
            String descripcion = prodDesc.getText().trim();
            String cantStr     = prodCantidad.getText().trim();
            String precStr     = prodPrecio.getText().trim();
            String almStr      = prodAlmacen.getText().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || cantStr.isEmpty() || precStr.isEmpty()) {
                mostrarError("Completa todos los campos obligatorios.");
                return;
            }

            Producto p = new Producto();
            p.nombre      = nombre;
            p.descripcion = descripcion;
            p.cantidad    = Integer.parseInt(cantStr);
            p.precio      = Double.parseDouble(precStr);
            p.almacenId   = almStr.isEmpty() ? 0 : Integer.parseInt(almStr);

            db.insertProducto(p, usuarioActual != null ? usuarioActual.nombre : "Sistema");

            prodNombre.clear(); prodDesc.clear();
            prodCantidad.clear(); prodPrecio.clear(); prodAlmacen.clear();

            cargarProductos();
            mostrarInfo("Producto agregado correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("Cantidad y precio deben ser valores numéricos.");
        } catch (SQLException e) {
            mostrarError("Error al agregar producto: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        try {
            Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
            if (sel == null) { mostrarError("Selecciona un producto para eliminar."); return; }
            if (confirmar("¿Eliminar producto?", "Se eliminará: " + sel.nombre)) {
                db.deleteProducto(sel.id);
                cargarProductos();
                mostrarInfo("Producto eliminado.");
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    // ========== CRUD ALMACENES ==========
    @FXML
    private void agregarAlmacen() {
        try {
            String nombre    = almNombre.getText().trim();
            String ubicacion = almUbicacion.getText().trim();
            if (nombre.isEmpty() || ubicacion.isEmpty()) { mostrarError("Completa todos los campos."); return; }

            db.insertAlmacen(nombre, ubicacion, usuarioActual != null ? usuarioActual.nombre : "Sistema");

            almNombre.clear(); almUbicacion.clear();
            cargarAlmacenes();
            mostrarInfo("Almacén agregado correctamente.");
        } catch (SQLException e) {
            mostrarError("Error al agregar almacén: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarAlmacen() {
        try {
            Almacen sel = tablaAlmacenes.getSelectionModel().getSelectedItem();
            if (sel == null) { mostrarError("Selecciona un almacén para eliminar."); return; }
            if (confirmar("¿Eliminar almacén?", "Se eliminará: " + sel.nombre)) {
                db.deleteAlmacen(sel.id);
                cargarAlmacenes();
                mostrarInfo("Almacén eliminado.");
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    // ========== CERRAR SESIÓN ==========
    @FXML
    private void handleLogout() {
        try {
            if (!confirmar("Cerrar sesión", "¿Seguro que deseas cerrar sesión?")) return;
            if (dbManager != null) dbManager.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) btnProductos.getScene().getWindow();
            stage.setTitle("Sistema de Inventario Unison - Iniciar sesión");
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(550);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== UTILIDADES ==========
    private boolean confirmar(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
