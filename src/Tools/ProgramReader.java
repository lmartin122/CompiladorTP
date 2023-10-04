package Tools;

import java.util.ArrayList;

public class ProgramReader {
    private ArrayList<ArrayList<Character>> program;
    private int currentLine;
    private int currentColumn;

    public ProgramReader(String p) {
        this.program = BinaryFileReader.read(p, "sample_programs");
        this.currentLine = 0;
        this.currentColumn = 0;
    }

    public Character character() {
        return program.get(currentLine).get(currentColumn);

    }

    public void next() {
        if (currentLine < program.size() && currentColumn < program.get(currentLine).size() - 1) {
            // Avanzamos a la siguiente columna en la misma línea
            currentColumn++;
        } else if (currentLine < program.size()) {
            // Avanzamos a la siguiente línea
            currentLine++;
            currentColumn = 0;
        }


    }

    public String programToString() {
        String out = "";
        int linea = 1;

        for (ArrayList<Character> l : this.program) {
            out += "[" + linea + "]: ";
            for (Character c : l) {
                out += c;
            }
            out += "\n";
            linea++;
        }

        return out;
    }

    public boolean hasProgram() {
        return this.program != null;
    }

    public void nextCol() {
        this.currentColumn++;
    }
    public void returnCharacter() {
        currentColumn = currentColumn - 1;
    }

    public boolean hasFinished() {
        if (currentLine == program.size()){ //si estoy en la ultima linea de codigo
            return true;
        }
        return false;
    }

    public int getCurrentLine() {
        return currentLine + 1;
    }

    public int getCurrentColumn() {
        return currentColumn + 1;
    }

}