package Tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BinaryFileReader {

    public static ArrayList<ArrayList<Character>> read(String fileName, String pathFile) {
        if (fileName.length() <= 1) {
            System.out.println("No ingreso el nombre del archivo correctamente.");
            return null;
        }

        String path = System.getProperty("user.dir");

        try {
            File file = new File(path + "/" + pathFile + "/" + fileName);

            if (!file.exists()) {
                System.out.println("El archivo no existe.");
                return null;
            }

            // Variables
            FileReader fileReader = new FileReader(file);
            int character;
            int line = 1;
            ArrayList<ArrayList<Character>> out = new ArrayList<>();
            out.add(new ArrayList<>());

            while ((character = fileReader.read()) != -1) {
                if ((char) character == '\n') {
                    if (out.get(line - 1).isEmpty()) {
                        out.get(line - 1).add((char) character);
                    }
                    out.add(new ArrayList<>());
                    line++;
                } else {
                    out.get(line - 1).add((char) character);
                }
            }
            out.get(line - 1).add('\n');

            fileReader.close();
            return out;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
