package compiler.GUI;

import compiler.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public CodeArea codeArea;
    public TextArea outputArea;

    private File currentFile = null;
    private boolean bufferChanged = false;
    private boolean fileSaved = false;

    @FXML
    private void newFileActionHandler(ActionEvent event) {
        if (this.codeArea.getLength() == 0 || !this.bufferChanged || this.fileSaved) {
            this.clearBuffers();
        } else {
            this.bufferChanged = true;
            this.handleFileSave();
            this.clearBuffers();
        }
        this.setTitle(null);
    }

    @FXML
    private void openFileActionHandler(ActionEvent event) {
        this.handleFileSave();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Abrir arquivo..");

        File result = chooser.showOpenDialog(null);
        if (result != null) {
            this.currentFile = result;
            this.clearTextAreas();
            this.setTitle(result.getName());

            try {
                Editor.writeBuffer(this.codeArea, this.currentFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            this.fileSaved = true;
            this.bufferChanged = false;
        }
    }

    @FXML
    private void saveActionHandler(ActionEvent event) {
        this.handleFileSave();
    }

    @FXML
    private void saveAsActionHandler(ActionEvent event) {
        this.saveAsFile();
    }

    @FXML
    public void handleTextCopy(ActionEvent event) {
        String selectedText = this.codeArea.getSelectedText();
        Editor.copy(selectedText);
        this.codeArea.deselect();
        this.codeArea.requestFocus();
    }

    @FXML
    public void handleTextPaste(ActionEvent event) {
        Editor.appendAfterCaret(this.codeArea, Editor.paste());
        this.codeArea.deselect();
        this.codeArea.requestFocus();
    }

    @FXML
    public void handleTextCut(ActionEvent event) {
        this.codeArea.cut();
        this.codeArea.requestFocus();
    }

    @FXML
    private void exitActionHandler(ActionEvent event) {
        String result = this.handleFileSave();
        if (result == "cancelar"){
            return;
        }
        Platform.exit();
    }

    private void setTitle(String title){
        if (title == null || title == ""){
            Main.getStage().setTitle("Compilador");
        }
        else {
            Main.getStage().setTitle("Compilador: " + title);
        }
    }


    private boolean isBufferOrFileChanged() {
        return this.fileSaved || !this.bufferChanged;
    }

    private String handleFileSave() {
        if (this.isBufferOrFileChanged()) {
            return null;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deseja salvar o arquivo?");
        alert.setHeaderText("O arquivo no editor foi editado. Deseja salvar?");

        ButtonType yesButton = new ButtonType("Sim");
        ButtonType noButton = new ButtonType("Não");
        ButtonType cancelButton = new ButtonType("cancelar");
        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == noButton) {
            this.clearBuffers();
            return noButton.getText();
        } else if (result.get() == yesButton) {
            if (this.currentFile != null) {
                this.saveFile();
            } else {
                this.saveAsFile();
            }
            return yesButton.getText();
        }
        return cancelButton.getText();
    }

    private void saveFile() {
        if (this.currentFile == null) {
            return;
        }

        try {
            IOManager.writeFile(this.currentFile, this.codeArea.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAsFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar arquivo");
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "2020.1", "*.txt", "*.djt", "*.cmp", "20201");

        chooser.getExtensionFilters().add(fileExtensions);

        File result = chooser.showSaveDialog(null);
        if (result != null) {
            this.currentFile = result;
            this.setTitle(result.getName());

            try {
                IOManager.writeFile(this.currentFile, this.codeArea.getText());
                this.bufferChanged = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void clearTextAreas() {
        this.outputArea.clear();
        this.codeArea.clear();
    }

    private void clearBuffers() {
        this.codeArea.clear();
        this.outputArea.clear();
        this.bufferChanged = false;
        this.fileSaved = false;
        this.currentFile = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));
        this.codeArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != newValue) {
                bufferChanged = true;
                fileSaved = false;
            }
        });
    }
}
