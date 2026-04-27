package mx.unison;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.unison.service.AlmacenService;
import mx.unison.service.AuthService;
import mx.unison.service.ProductoService;
import mx.unison.service.UsuarioService;

/**
 * Controlador del panel principal.
 *
 * Responsabilidades (solo UI):
 *  - Enlazar columnas de tablas con los modelos
 *  - Leer campos de texto y pasarlos a los servicios
 *  - Mostrar resultados, errores y confirmaciones
 *  - Gestionar visibilidad de vistas según el rol (via AuthService)
 *
 * NO valida reglas de negocio ni accede directamente a la BD.
 * Toda lógica vive en ProductoService, AlmacenService, UsuarioService.
 */
public class MainController {

    // ── FXML: navegación ─────────────────────────────────────────────────────
    @FXML private Button btnProductos;
    @FXML private Button btnAlmacenes;
    @FXML private Button btnUsuarios;

    // ── FXML: vistas ─────────────────────────────────────────────────────────
    @FXML private VBox productosView;
    @FXML private VBox almacenesView;
    @FXML private VBox usuariosView;

    // ── FXML: tabla productos ─────────────────────────────────────────────────
    @FXML private TableView<Producto>           tablaProductos;
    @FXML private TableColumn<Producto, Integer> colProdId;
    @FXML private TableColumn<Producto, String>  colProdNombre;
    @FXML private TableColumn<Producto, String>  colProdDesc;
    @FXML private TableColumn<Producto, Integer> colProdCantidad;
    @FXML private TableColumn<Producto, Double>  colProdPrecio;
    @FXML private TableColumn<Producto, String>  colProdAlmacen;
    @FXML private TableColumn<Producto, String>  colProdUsuario;

    // ── FXML: campos nuevo producto ───────────────────────────────────────────
    @FXML private TextField txtSearchProductos;
    @FXML private TextField prodNombre;
    @FXML private TextField prodDesc;
    @FXML private TextField prodCantidad;
    @FXML private TextField prodPrecio;
    @FXML private TextField prodAlmacen;

    // ── FXML: tabla almacenes ─────────────────────────────────────────────────
    @FXML private TableView<Almacen>            tablaAlmacenes;
    @FXML private TableColumn<Almacen, Integer>  colAlmId;
    @FXML private TableColumn<Almacen, String>   colAlmNombre;
    @FXML private TableColumn<Almacen, String>   colAlmUbicacion;
    @FXML private TableColumn<Almacen, String>   colAlmUsuario;

    // ── FXML: campos nuevo almacén ────────────────────────────────────────────
    @FXML private TextField txtSearchAlmacenes;
    @FXML private TextField almNombre;
    @FXML private TextField almUbicacion;

    // ── FXML: tabla usuarios ──────────────────────────────────────────────────
    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario, String>  colUsuNombre;
    @FXML private TableColumn<Usuario, String>  colUsuRol;

    @FXML private TextField txtSearchUsuarios;

    // ── Servicios ─────────────────────────────────────────────────────────────
    private AuthService     authService;
    private ProductoService productoService;
    @FXML private AlmacenService  almacenService;
    private UsuarioService  usuarioService;

    // ── Inicialización ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarBusquedaEnVivo();
    }

    /**
     * Punto de entrada llamado por LoginController después del login exitoso.
     * Recibe el AuthService ya autenticado y construye los demás servicios.
     */
    public void inicializar(AuthService authService) {
        this.authService = authService;

        Database db = new Database("jdbc:sqlite:InventarioBD.db");
        this.productoService = new ProductoService(db);
        this.almacenService  = new AlmacenService(db);

        try {
            DatabaseManager dbManager = new DatabaseManager();
            this.usuarioService = new UsuarioService(dbManager);
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
        }

        aplicarPermisosPorRol();
        recargarTodo();

        // Vista inicial según rol
        if (authService.puedeGestionarProductos()) {
            showProductos();
        } else {
            showAlmacenes();
        }
    }

    // ── Permisos ──────────────────────────────────────────────────────────────

    private void aplicarPermisosPorRol() {
        // Deshabilitar botones según lo que el rol NO puede ver
        if (!authService.puedeGestionarProductos()) {
            btnProductos.setDisable(true);
        }
        if (!authService.puedeGestionarAlmacenes()) {
            btnAlmacenes.setDisable(true);
        }
        if (!authService.esAdmin()) {
            btnUsuarios.setDisable(true);
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    @FXML private void showProductos() {
        activarVista(productosView, btnProductos);
    }

    @FXML private void showAlmacenes() {
        activarVista(almacenesView, btnAlmacenes);
    }

    @FXML private void showUsuarios() {
        activarVista(usuariosView, btnUsuarios);
    }

    private void activarVista(VBox vista, Button boton) {
        productosView.setVisible(false); productosView.setManaged(false);
        almacenesView.setVisible(false); almacenesView.setManaged(false);
        usuariosView.setVisible(false);  usuariosView.setManaged(false);
        vista.setVisible(true);          vista.setManaged(true);

        btnProductos.getStyleClass().remove("nav-button-active");
        btnAlmacenes.getStyleClass().remove("nav-button-active");
        btnUsuarios.getStyleClass().remove("nav-button-active");
        boton.getStyleClass().add("nav-button-active");
    }

    // ── Configuración de columnas (solo UI) ───────────────────────────────────

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

    // ── Búsqueda en vivo ──────────────────────────────────────────────────────

    private void configurarBusquedaEnVivo() {
        if (txtSearchProductos != null)
            txtSearchProductos.textProperty().addListener(
                (obs, anterior, nuevo) -> refrescarTablaProductos(nuevo));

        if (txtSearchAlmacenes != null)
            txtSearchAlmacenes.textProperty().addListener(
                (obs, anterior, nuevo) -> refrescarTablaAlmacenes(nuevo));

        if (txtSearchUsuarios != null)
            txtSearchUsuarios.textProperty().addListener(
                (obs, anterior, nuevo) -> refrescarTablaUsuarios(nuevo));
    }

    // ── Carga / refresco de tablas ────────────────────────────────────────────

    private void recargarTodo() {
        refrescarTablaProductos(null);
        refrescarTablaAlmacenes(null);
        if (authService.esAdmin()) {
            refrescarTablaUsuarios(null);
        }
    }

    private void refrescarTablaProductos(String filtro) {
        if (productoService == null) return;
        tablaProductos.setItems(
            FXCollections.observableArrayList(productoService.buscar(filtro)));
    }

    private void refrescarTablaAlmacenes(String filtro) {
        if (almacenService == null) return;
        tablaAlmacenes.setItems(
            FXCollections.observableArrayList(almacenService.buscar(filtro)));
    }

    private void refrescarTablaUsuarios(String filtro) {
        if (usuarioService == null) return;
        tablaUsuarios.setItems(
            FXCollections.observableArrayList(usuarioService.buscar(filtro)));
    }

    @FXML private void refreshProductos() { refrescarTablaProductos(txtSearchProductos.getText()); }
    @FXML private void refreshAlmacenes() { refrescarTablaAlmacenes(txtSearchAlmacenes.getText()); }
    @FXML private void refreshUsuarios()  { refrescarTablaUsuarios(txtSearchUsuarios.getText());   }

    // ── CRUD Productos ────────────────────────────────────────────────────────

    @FXML
    private void agregarProducto() {
        try {
            productoService.agregar(
                prodNombre.getText(),
                prodDesc.getText(),
                prodCantidad.getText(),
                prodPrecio.getText(),
                prodAlmacen.getText(),
                authService.getNombreUsuario()
            );
            limpiarCamposProducto();
            refrescarTablaProductos(txtSearchProductos.getText());
            mostrarInfo("Producto agregado correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        try {
            productoService.eliminar(sel); // lanza si sel == null
            if (!confirmar("¿Eliminar producto?", "Se eliminará: " + sel.nombre)) return;
            productoService.eliminar(sel);
            refrescarTablaProductos(txtSearchProductos.getText());
            mostrarInfo("Producto eliminado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarCamposProducto() {
        prodNombre.clear(); prodDesc.clear();
        prodCantidad.clear(); prodPrecio.clear(); prodAlmacen.clear();
    }

    // ── CRUD Almacenes ────────────────────────────────────────────────────────

    @FXML
    private void agregarAlmacen() {
        try {
            almacenService.agregar(
                almNombre.getText(),
                almUbicacion.getText(),
                authService.getNombreUsuario()
            );
            almNombre.clear(); almUbicacion.clear();
            refrescarTablaAlmacenes(txtSearchAlmacenes.getText());
            mostrarInfo("Almacén agregado correctamente.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarAlmacen() {
        Almacen sel = tablaAlmacenes.getSelectionModel().getSelectedItem();
        try {
            almacenService.eliminar(sel); // lanza si sel == null
            if (!confirmar("¿Eliminar almacén?", "Se eliminará: " + sel.nombre)) return;
            almacenService.eliminar(sel);
            refrescarTablaAlmacenes(txtSearchAlmacenes.getText());
            mostrarInfo("Almacén eliminado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ── Cerrar sesión ─────────────────────────────────────────────────────────

    @FXML
    private void handleLogout() {
        if (!confirmar("Cerrar sesión", "¿Seguro que deseas cerrar sesión?")) return;

        authService.logout();

        try {
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

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private boolean confirmar(String titulo, String contenido) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(contenido);
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
