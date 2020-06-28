package compiler.GUI;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PopupController {
    @FXML
    public TextArea instructionPopup = new TextArea();

    public void clean(){
        this.instructionPopup.clear();
    }

    public void write(String stream){
        this.instructionPopup.appendText(stream);
    }

    public void popup(){
        Stage newStage = new Stage();
        VBox comp = new VBox();
        comp.getChildren().add(this.instructionPopup);
        Scene stageScene = new Scene(comp, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }
}
