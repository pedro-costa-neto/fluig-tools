package br.com.softdigital.fluig;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Fluig Application
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class FluigApplication extends Application {

    private final static Logger LOG = Logger.getLogger(FluigApplication.class);

    public static void main(String[] args) {
        LOG.info("Iniciando aplicacao");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(FluigApplication.class.getResource("/layouts/FrmPrincipal.fxml"));
        
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Fluig - Pincipal");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
        stage.show();
    }
}
