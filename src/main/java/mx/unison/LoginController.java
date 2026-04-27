package mx.unison;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private Database db;

    @FXML
    public void initialize() {
        db = new Database("jdbc:sqlite:InventarioBD.db");
        lblError.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String nombre = txtUsuario.getText().trim().toUpperCase();
        String password = txtPassword.getText();

        lblError.setVisible(false);

        if (nombre.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        Usuario usuario = db.authenticate(nombre, password);

        if (usuario != null) {
            abrirPanelPrincipal(usuario);
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    private void abrirPanelPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/main_view.fxml")
            );
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setUsuarioActual(usuario);

            Scene scene = new Scene(root);
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Sistema de Inventario Unison — " + usuario.nombre + " (" + usuario.rol + ")");
            stage.setScene(scene);
            stage.setWidth(1100);
            stage.setHeight(750);
            stage.centerOnScreen();
        } catch (Exception e) {
            mostrarError("Error al abrir el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}
