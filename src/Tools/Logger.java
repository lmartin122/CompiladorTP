package Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class Logger {

    private static final String LOG_FILE = "/log.txt";
    private static final ArrayList<String> warnings = new ArrayList<>();
    private static final ArrayList<String> errors = new ArrayList<>();

    private Logger() {
    };

    private enum LogType {
        ERROR,
        WARNING
    }

    public static void logError(int line, String message) {
        errors.add(LogType.ERROR + " en la linea " + line + ": " + message + ".\n");
    }

    public static void logWarning(int line, String message) {
        warnings.add(LogType.WARNING + " en la linea " + line + ": " + message + ".\n");
    }

    public static void dumpLog() {
        String path = System.getProperty("user.dir");

        try (FileWriter fileWriter = new FileWriter(path + LOG_FILE)) {
            writeLogEntries(fileWriter, warnings);
            writeLogEntries(fileWriter, errors);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLogEntries(FileWriter fileWriter, ArrayList<String> entries) throws IOException {
        for (String s : entries) {
            fileWriter.write(s);
        }
    }

}
