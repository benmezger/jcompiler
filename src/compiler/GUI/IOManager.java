package compiler.GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOManager {

    public static File writeFile(File file, String data) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write(data + "\n");
        writer.close();
        return file;
    }
}
