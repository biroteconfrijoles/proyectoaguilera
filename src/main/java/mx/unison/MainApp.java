package mx.unison;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Cargar la pantalla de LOGIN (no el panel principal)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Aplicar estilos CSS
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Sistema de Inventario Unison - Iniciar sesión");
            primaryStage.setScene(scene);
            primaryStage.setWidth(900);
            primaryStage.setHeight(550);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            primaryStage.setOnCloseRequest(e -> System.exit(0));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la interfaz: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
