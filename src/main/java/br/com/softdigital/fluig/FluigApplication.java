package br.com.softdigital.fluig;

import javafx.stage.StageStyle;
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

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(FluigApplication.class.getResource("/layouts/FrmPrincipal.fxml"));
        
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Fluig Tools - Pincipal");
        stage.setResizable(false);
        //stage.initStyle(StageStyle.UTILITY);
        stage.getIcons().add(new Image("/images/fluig-icon-brand.png"));
        stage.show();
    }
}
