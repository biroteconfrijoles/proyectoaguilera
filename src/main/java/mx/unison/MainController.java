package mx.unison;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    
    // Tablas
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableView<Almacen> tablaAlmacenes;
    @FXML private TableView<Usuario> tablaUsuarios;
    
    // TextFields de búsqueda
    @FXML private TextField txtSearchProductos;
    @FXML private TextField txtSearchAlmacenes;
    @FXML private TextField txtSearchUsuarios;
    
    // TextFields para agregar productos
    @FXML private TextField prodNombre;
    @FXML private TextField prodDesc;
    @FXML private TextField prodCantidad;
    @FXML private TextField prodPrecio;
    @FXML private TextField prodAlmacen;
    
    // TextFields para agregar almacenes
    @FXML private TextField almNombre;
    @FXML private TextField almUbicacion;
    
    // ========== VARIABLES DE INSTANCIA ==========
    private DatabaseManager dbManager;
    private Usuario usuarioActual; // Se establecería después del login

    @FXML
    public void initialize() {
        try {
            dbManager = new DatabaseManager();
            cargarProductos();
            cargarAlmacenes();
            cargarUsuarios();
        } catch (SQLException e) {
            mostrarError("Error al inicializar base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== NAVEGACIÓN ==========
    @FXML
    private void showProductos() {
        mostrarVista(productosView);
        actualizarBotonActivo(btnProductos);
    }

    @FXML
    private void showAlmacenes() {
        mostrarVista(almacenesView);
        actualizarBotonActivo(btnAlmacenes);
    }

    @FXML
    private void showUsuarios() {
        mostrarVista(usuariosView);
        actualizarBotonActivo(btnUsuarios);
    }

    private void mostrarVista(VBox vista) {
        productosView.setVisible(false);
        productosView.setManaged(false);
        almacenesView.setVisible(false);
        almacenesView.setManaged(false);
        usuariosView.setVisible(false);
        usuariosView.setManaged(false);
        
        vista.setVisible(true);
        vista.setManaged(true);
    }

    private void actualizarBotonActivo(Button botonActivo) {
        btnProductos.getStyleClass().remove("nav-button-active");
        btnAlmacenes.getStyleClass().remove("nav-button-active");
        btnUsuarios.getStyleClass().remove("nav-button-active");
        
        botonActivo.getStyleClass().add("nav-button-active");
    }

    // ========== CARGAR DATOS EN TABLAS ==========
    @FXML
    private void cargarProductos() throws SQLException {
        List<Producto> productos = dbManager.getProductoDao().queryForAll();
        tablaProductos.setItems(FXCollections.observableArrayList(productos));
    }

    @FXML
    private void cargarAlmacenes() throws SQLException {
        List<Almacen> almacenes = dbManager.getAlmacenDao().queryForAll();
        tablaAlmacenes.setItems(FXCollections.observableArrayList(almacenes));
    }

    @FXML
    private void cargarUsuarios() throws SQLException {
        List<Usuario> usuarios = dbManager.getUsuarioDao().queryForAll();
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML
    private void refreshProductos() {
        try {
            cargarProductos();
            mostrarInfo("Productos actualizados");
        } catch (SQLException e) {
            mostrarError("Error al actualizar productos: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAlmacenes() {
        try {
            cargarAlmacenes();
            mostrarInfo("Almacenes actualizados");
        } catch (SQLException e) {
            mostrarError("Error al actualizar almacenes: " + e.getMessage());
        }
    }

    @FXML
    private void refreshUsuarios() {
        try {
            cargarUsuarios();
            mostrarInfo("Usuarios actualizados");
        } catch (SQLException e) {
            mostrarError("Error al actualizar usuarios: " + e.getMessage());
        }
    }

    // ========== OPERACIONES CRUD - PRODUCTOS ==========
    @FXML
    private void agregarProducto() {
        try {
            String nombre = prodNombre.getText().trim();
            String descripcion = prodDesc.getText().trim();
            String cantidadStr = prodCantidad.getText().trim();
            String precioStr = prodPrecio.getText().trim();
            String almacenIdStr = prodAlmacen.getText().trim();
            
            if (nombre.isEmpty() || descripcion.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
                mostrarError("Por favor completa todos los campos");
                return;
            }
            
            Producto producto = new Producto();
            producto.nombre = nombre;
            producto.descripcion = descripcion;
            producto.cantidad = Integer.parseInt(cantidadStr);
            producto.precio = Double.parseDouble(precioStr);
            producto.almacenId = almacenIdStr.isEmpty() ? 0 : Integer.parseInt(almacenIdStr);
            producto.ultimoUsuario = usuarioActual != null ? usuarioActual.nombre : "Sistema";
            
            dbManager.getProductoDao().create(producto);
            
            prodNombre.clear();
            prodDesc.clear();
            prodCantidad.clear();
            prodPrecio.clear();
            prodAlmacen.clear();
            
            cargarProductos();
            mostrarInfo("Producto agregado exitosamente");
        } catch (NumberFormatException e) {
            mostrarError("Valores numéricos inválidos");
        } catch (SQLException e) {
            mostrarError("Error al agregar producto: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        try {
            Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarError("Selecciona un producto para eliminar");
                return;
            }
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar producto?");
            confirmacion.setContentText("¿Estás seguro de que deseas eliminar el producto: " + seleccionado.nombre + "?");
            
            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                dbManager.getProductoDao().delete(seleccionado);
                cargarProductos();
                mostrarInfo("Producto eliminado");
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar producto: " + e.getMessage());
        }
    }

    // ========== OPERACIONES CRUD - ALMACENES ==========
    @FXML
    private void agregarAlmacen() {
        try {
            String nombre = almNombre.getText().trim();
            String ubicacion = almUbicacion.getText().trim();
            
            if (nombre.isEmpty() || ubicacion.isEmpty()) {
                mostrarError("Por favor completa todos los campos");
                return;
            }
            
            Almacen almacen = new Almacen();
            almacen.nombre = nombre;
            almacen.ubicacion = ubicacion;
            almacen.ultimoUsuario = usuarioActual != null ? usuarioActual.nombre : "Sistema";
            
            dbManager.getAlmacenDao().create(almacen);
            
            almNombre.clear();
            almUbicacion.clear();
            
            cargarAlmacenes();
            mostrarInfo("Almacén agregado exitosamente");
        } catch (SQLException e) {
            mostrarError("Error al agregar almacén: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarAlmacen() {
        try {
            Almacen seleccionado = tablaAlmacenes.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarError("Selecciona un almacén para eliminar");
                return;
            }
            
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Eliminar almacén?");
            confirmacion.setContentText("¿Estás seguro de que deseas eliminar el almacén: " + seleccionado.nombre + "?");
            
            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                dbManager.getAlmacenDao().delete(seleccionado);
                cargarAlmacenes();
                mostrarInfo("Almacén eliminado");
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar almacén: " + e.getMessage());
        }
    }

    // ========== CERRAR SESIÓN ==========
    @FXML
    private void handleLogout() {
        try {
            if (dbManager != null) {
                dbManager.close();
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== UTILIDADES ==========
    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
