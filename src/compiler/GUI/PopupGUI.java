package compiler.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PopupGUI {
    private Stage stage;

    PopupGUI(String title){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            System.out.println(fxmlLoader);
            fxmlLoader.setLocation(getClass().getResource("vm-window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            this.stage = new Stage();
            this.stage.setTitle(title);
            this.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void open(){
        this.stage.show();
    }


}
