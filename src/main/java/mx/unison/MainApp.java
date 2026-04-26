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
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_view.fxml"));
            Parent root = loader.load();
            
            // Crear la escena
            Scene scene = new Scene(root);
            
            // Aplicar estilos CSS
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configurar el escenario
            primaryStage.setTitle("Sistema de Inventario Unison - JavaFX");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1100);
            primaryStage.setHeight(750);
            primaryStage.setOnCloseRequest(e -> {
                try {
                    // Aquí podrías cerrar la conexión a BD si es necesario
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            
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