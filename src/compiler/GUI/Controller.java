package compiler.GUI;

import compiler.parser.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    public Button saveButton;

    private File currentFile = null;
    private boolean bufferChanged = false;
    private boolean fileSaved = false;

    @FXML
    private void newFileActionHandler(ActionEvent event) {
        if (this.codeArea.getLength() == 0 || !this.bufferChanged || this.fileSaved) {
            this.clearBuffers();
        } else {
            this.bufferChanged = true;

            if (this.currentFile != null) {
                String result = this.requestUserAuthorizationToSaveFile();
                if (result == "Sim") {
                    this.saveFile();
                }
                if (result == "cancelar") {
                    return;
                }
            }
            else {
                this.handleFileSave();
            }
            this.clearBuffers();
            this.setTitle(null);
        }
    }

    private String requestUserAuthorizationToSaveFile(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deseja salvar o arquivo?");
        alert.setHeaderText("O arquivo no editor foi editado. Deseja salvar?");

        ButtonType yesButton = new ButtonType("Sim");
        ButtonType noButton = new ButtonType("Não");
        ButtonType cancelButton = new ButtonType("cancelar");
        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        ButtonType buttonType = result.get();

        if (yesButton.equals(buttonType)) {
            return yesButton.getText();
        }
        else if (noButton.equals(buttonType)) {
            return noButton.getText();
        }
        else  {
            return cancelButton.getText();
        }
    }

    @FXML
    private void openFileActionHandler(ActionEvent event) {
        if (this.currentFile != null && this.bufferChanged) {
            String result = this.requestUserAuthorizationToSaveFile();
            if (result == "Sim") {
                this.saveFile();
            }
            if (result == "cancelar"){
                return;
            }
        }

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

            this.saveButton.setDisable(true);
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
        if (this.codeArea.getLength() == 0 || !this.bufferChanged || this.fileSaved) {
            Platform.exit();
        }
        else {
            String result = this.requestUserAuthorizationToSaveFile();
            if (result == "Sim") {
                if (this.currentFile != null && this.bufferChanged) {
                    this.saveFile();
                } else {
                    this.saveAsFile();
                }
            } else if (result == "cancelar") {
                return;
            }
        }
        Platform.exit();
    }

    @FXML
    private void compileActionHandler(ActionEvent event){
        this.outputArea.clear();
        boolean matched = false;

        for (Token token: LanguageParser.getTokens(this.codeArea.getText())) {
            if (token.kind == LanguageParserConstants.OTHER){
                this.outputArea.appendText("Token inválido "  + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + "): ");
            }
            else if (token.kind == 5){
                this.outputArea.appendText("Erro: Comentário de bloco não encerrado");
            }
            else {
                this.outputArea.appendText("Token (" + token.kind + "): " + LanguageParserConstants.tokenImage[token.kind]);
            }

            if (token.kind != 0 && !LanguageParser.tokenImage[token.kind].equals("\"" + token.image + "\"")) {
                this.outputArea.appendText(" \"" + token.image + "\"" + " na linha " + token.beginLine + " coluna " + token.beginColumn + "\n");
            }
            else {
                this.outputArea.appendText(" na linha " + token.beginLine + " coluna " + token.beginColumn + "\n");
            }
        }
    }

    private void setTitle(String title){
        if (title == null || title == ""){
            CompilerApp.getStage().setTitle("Compilador");
        }
        else {
            CompilerApp.getStage().setTitle("Compilador: " + title);
        }
    }


    private boolean isBufferOrFileChanged() {
        return !this.fileSaved || this.bufferChanged;
    }

    private String handleFileSave() {
        if (!this.isBufferOrFileChanged()) {
            return null;
        }

        if (this.currentFile != null){
            this.saveFile();
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
            this.fileSaved = true;
            this.saveButton.setDisable(true);
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
                this.fileSaved = true;
                this.saveButton.setDisable(true);
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
        this.saveButton.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));
        this.saveButton.setDisable(true);
        this.codeArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != newValue) {
                saveButton.setDisable(false);
                bufferChanged = true;
                if (currentFile != null) {
                    fileSaved = false;
                }
            }
        });
    }
}
