package Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class Logger {

    private static final String LOG_FILE = "/output/log.txt";
    private static final ArrayList<String> warnings = new ArrayList<>();
    private static final ArrayList<String> errors = new ArrayList<>();
    private static final ArrayList<String> tokens = new ArrayList<>();
    private static final ArrayList<String> rules = new ArrayList<>();

    private Logger() {
    };

    private enum LogType {
        ERROR,
        WARNING,
        TOKEN,
        RULE
    }

    public static void logError(int line, Object message) {
        errors.add("Se encontro un " + LogType.ERROR + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logWarning(int line, Object message) {
        warnings.add("Se encontro un " + LogType.WARNING + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logToken(int line, Object message) {
        tokens.add("Se encontro un " + LogType.TOKEN + " en la linea [" + line + "] : " + message + "\n");
    }

    public static void logRule(int line, Object message) {
        rules.add("Se encontro un " + LogType.RULE + " en la linea [" + line + "] : " + message + "\n");
    }

    public static String dumpLog() throws IOException {
        String path = System.getProperty("user.dir");

        String log = generateLog();

        try (FileWriter fileWriter = new FileWriter(path + LOG_FILE)) {
            fileWriter.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return log;
    }

    private static String generateLog() {
        String out = "";

        out = ">>>    LOG \n";

        for (String s : warnings) {
            out += s;
        }

        for (String s : errors) {
            out += s;
        }

        for (String s : tokens) {
            out += s;
        }

        for (String s : rules) {
            out += s;
        }

        out += ">>>    TABLA DE SIMBOLOS\n";

        out += TablaSimbolos.printTable();

        return out;
    }

}
