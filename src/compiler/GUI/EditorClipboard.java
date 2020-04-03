package compiler.GUI;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class EditorClipboard {
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public void copyToClipboard(StringSelection selection) {
        this.clipboard.setContents(selection, selection);
    }

    public String getLast() throws IOException, UnsupportedFlavorException {
        return (String) this.clipboard.getData(DataFlavor.stringFlavor);
    }

}
