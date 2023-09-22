package Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logger {

    private static final String LOG_FILE = "/log.txt";
    private ArrayList<String> warnings;
    private ArrayList<String> errors;

    public Logger() {
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    private enum LogType {
        ERROR,
        WARNING
    }

    public void logError(int line, String message) {
        errors.add(LogType.ERROR + " en la linea " + line + ": " + message + ".\n");
    }

    public void logWarning(int line, String message) {
        warnings.add(LogType.WARNING + " en la linea " + line + ": " + message + ".\n");
    }

    public void dumpLog() {
        String path = System.getProperty("user.dir");

        try (FileWriter fileWriter = new FileWriter(path + LOG_FILE)) {
            writeLogEntries(fileWriter, warnings);
            writeLogEntries(fileWriter, errors);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLogEntries(FileWriter fileWriter, ArrayList<String> entries) throws IOException {
        for (String s : entries) {
            fileWriter.write(s);
        }
    }

}
