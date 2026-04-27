package mx.unison;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.unison.service.AuthService;

/**
 * Controlador de la pantalla de login.
 *
 * Responsabilidades (solo UI):
 *  - Leer los campos del formulario
 *  - Delegar la autenticación a AuthService
 *  - Mostrar errores o navegar al panel principal
 *
 * NO contiene lógica de negocio ni acceso directo a la BD.
 */
public class LoginController {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;

    // ── Servicios ─────────────────────────────────────────────────────────────
    private AuthService authService;

    @FXML
    public void initialize() {
        authService = new AuthService(new Database("jdbc:sqlite:InventarioBD.db"));
        lblError.setVisible(false);
    }

    // ── Acciones UI ───────────────────────────────────────────────────────────

    @FXML
    private void handleLogin() {
        ocultarError();

        String nombre   = txtUsuario.getText();
        String password = txtPassword.getText();

        // Validación básica de campos vacíos (responsabilidad de la UI)
        if (nombre.isBlank() || password.isBlank()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        // Delegar autenticación al servicio
        boolean exitoso = authService.login(nombre, password);

        if (exitoso) {
            navegarAlPanelPrincipal(authService);
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private void navegarAlPanelPrincipal(AuthService authService) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_view.fxml"));
            Parent root = loader.load();

            // Pasar el AuthService ya autenticado al controlador principal
            MainController controller = loader.getController();
            controller.inicializar(authService);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Inventario Unison — "
                    + authService.getNombreUsuario()
                    + " (" + authService.getUsuarioActual().rol + ")");
            stage.setScene(scene);
            stage.setWidth(1100);
            stage.setHeight(750);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarError("Error al abrir el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Helpers de UI ────────────────────────────────────────────────────────

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
    }
}
