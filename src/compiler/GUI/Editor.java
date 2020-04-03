package compiler.GUI;

import javafx.scene.control.IndexRange;
import org.fxmisc.richtext.CodeArea;

import javax.swing.text.BadLocationException;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Editor {
    private static final EditorClipboard clipboard = new EditorClipboard();

    public static void writeBuffer(CodeArea area, File file) throws FileNotFoundException, BadLocationException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            area.appendText(line + "\n");
        }
    }

    public static void appendAfterCaret(CodeArea area, String text) {
        area.insertText(area.getCaretPosition(), text);
    }


    public static void copy(String s) {
        try {
            if (clipboard.getLast() != s) {
                clipboard.copyToClipboard(new StringSelection(s));
            }
        } catch (IOException | UnsupportedFlavorException e) {
            return;
        }
    }

    public static String paste() {
        try {
            return clipboard.getLast();
        } catch (IOException | UnsupportedFlavorException e) {
            return "";
        }
    }
}

