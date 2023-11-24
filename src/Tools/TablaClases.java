package Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import GCodigo.Scope;

public class TablaClases {

    private static HashMap<String, HashMap<String, ArrayList<String>>> classes = new HashMap<>();

    private static final String ATRIBUTOS = "Atributos";
    private static final String ATRIBUTOS_TIPO_CLASE = "Atributos_tipo_clase";
    private static final String HERENCIA = "HeredaDe";
    // private static final String IMPLEMENTA = "implementaDe";
    private static final String METODOS = "Metodos";
    private static final String IMPL_FOR = "a_implementar";

    public static final String TYPE_SEPARATOR = ":";

    private interface Lambda {
        ArrayList<String> invoke(String _class);

    }

    public static void addClase(String _class) {
        HashMap<String, ArrayList<String>> attributes = new HashMap<>();
        classes.put(_class, attributes);
    };

    public static HashMap<String, ArrayList<String>> getClase(String c) {
        if (existeClase(c)) {
            return classes.get(c);
        }

        return null;
    }

    private static void acomodarLista(ArrayList<String> methods) {
        for (int i = 0; i < methods.size(); i++) {
            String method_c = methods.get(i);
            String[] parts = method_c.split(TYPE_SEPARATOR);
            String method = parts[0];
            String type = TablaSimbolos.getTypeLexema(parts[1]);

            methods.set(i, (type.isEmpty()) ? method_c : method + TYPE_SEPARATOR + type);
        }

    };

    public static boolean implementaMetodosInterfaz(String _class, String _interface) {

        ArrayList<String> methodsToImplemented = getAllMetodosIMPL(_interface);
        ArrayList<String> methodsImplemented = getAllMetodos(_class);

        acomodarLista(methodsImplemented);
        acomodarLista(methodsToImplemented);

        // System.out.println(_class + " implementa " + _interface);
        // System.out.println("Existe la clase a implementar? " +
        // existeClase(_interface));
        // System.out.println("Metodos a implementar: " +
        // methodsToImplemented.toString());
        // System.out.println("Metodos implementados: " + methodsImplemented);

        for (String method : methodsToImplemented) {
            if (!methodsImplemented.contains(method))
                return false;
        }

        return true;
    }

    public static boolean existeClase(String clase) {
        return classes.containsKey(clase);
    }

    public static void addInterface(String interfaz) {
        addClase(interfaz);
    }

    private static void addAttribute(String _attribute, String _class, String _component) {
        if (existeClase(_class)) {
            ArrayList<String> component = classes.get(_class).get(_component);
            if (component == null) {
                classes.get(_class).put(_component, new ArrayList<>());
                component = classes.get(_class).get(_component);
            }
            component.add(_attribute);

        }
        return;
    }

    private static void removeAttribute(String _attribute, String _class, String _component) {
        if (existeClase(_class) && containsAttribute(_attribute, _class, _component))
            classes.get(_class).get(_component).remove(_attribute);
        return;
    }

    private static ArrayList<String> getAttribute(String _class, String _component) {
        if (existeClase(_class)) {
            ArrayList<String> component = classes.get(_class).get(_component);
            if (component != null) {
                return component;
            }
        }
        return new ArrayList<>();
    }

    private static String getType(String _subClass, String _attribute, String _class) {
        if (!TablaSimbolos.isClass(_subClass + Scope.getScopeMain()))
            _subClass = TablaSimbolos.getTypeLexema(_subClass + Scope.getScopeMain() + Scope.SEPARATOR + _class);

        return getType(_attribute, _subClass);
    }

    private static String getType(String _class, String _attribute) {

        String type = null;

        type = TablaSimbolos.getTypeLexema(_attribute + Scope.getScopeMain() + Scope.SEPARATOR + _class);

        if (type == null || type.isEmpty()) {
            type = TablaSimbolos.getParameter(_attribute + Scope.getScopeMain() + Scope.SEPARATOR + _class);
            if (type == null)
                return "";
        }

        return TYPE_SEPARATOR + type;
    }

    private static ArrayList<String> getAllAttribute(String _class, Lambda f, Lambda g) {
        ArrayList<String> out = new ArrayList<>();

        Stack<String> hereda = new Stack<>();

        for (String a : getAtributosTC(_class)) {
            String[] parts = a.split(Scope.SEPARATOR);

            ArrayList<String> TC_attributes = getAllAttribute(parts[0], f, g);

            for (String TC_attribute : TC_attributes) {
                out.add("." + parts[1] + "." + TC_attribute + getType(parts[1], TC_attribute, _class));
            }
        }

        for (String p : getHerencia(_class)) {
            hereda.push(p);
        }

        while (!hereda.isEmpty()) {

            String relative = hereda.pop();

            for (String family : getHerencia(relative))
                hereda.push(family);

            ArrayList<String> r_attributes = f.invoke(relative); // Generalizarlo

            for (String attribute : r_attributes) {
                out.add(relative + "." + attribute + getType(relative, attribute, _class));

            }

        }

        for (String attribute : g.invoke(_class)) {
            out.add(attribute + getType(_class, attribute));

        }

        return out;
    }

    private static boolean containsMyAttributes(String relative, String _class) {
        if (containsComponent(relative, ATRIBUTOS)) {
            if (containsComponent(_class, ATRIBUTOS)) {
                ArrayList<String> my_attributes = classes.get(_class).get(ATRIBUTOS);
                for (String a : classes.get(relative).get(ATRIBUTOS)) {
                    if (my_attributes.contains(a))
                        return true;
                }
            }
            return false;
        }

        return false;
    }

    private static boolean containsAttribute(String _attribute, String _class, String _component) {
        if (containsComponent(_class, _component)) {
            return classes.get(_class).get(_component).contains(_attribute);
        }

        return false;
    }

    private static boolean containsComponent(String _class, String _component) {
        if (existeClase(_class)) {
            ArrayList<String> component = classes.get(_class).get(_component);
            if (component == null)
                return false;
            return true;
        }

        return false;
    }

    public static void addMetodo(String _method, String _class) {
        addAttribute(_method, _class, METODOS);
    }

    public static ArrayList<String> getMetodos(String _class) {
        return getAttribute(_class, METODOS);
    }

    public static ArrayList<String> getAllMetodos(String _class) {
        return getAllAttribute(_class, (e) -> getAllMetodos(e), (e) -> getMetodos(e));
    }

    public static void addMetodoIMPL(String _method, String _class) {
        addAttribute(_method, _class, IMPL_FOR);
    }

    public static void setMetodoIMPL(String _method, String _class) {
        String parts[] = _method.split(":");
        _method = parts[0];
        removeAttribute(_method, _class, IMPL_FOR);
        addAttribute(_method, _class, METODOS);
    }

    public static ArrayList<String> getMetodoIMPL(String _class) {
        return getAttribute(_class, IMPL_FOR);
    }

    public static ArrayList<String> getAllMetodosIMPL(String _class) {
        return getAllAttribute(_class, (e) -> getAllMetodos(e), (e) -> getMetodoIMPL(e));
    }

    public static void addAtributo(String _attribute, String _class) {
        addAttribute(_attribute, _class, ATRIBUTOS);
    }

    public static void addAtributos(String _attributes, String _class) {
        String[] parts = _attributes.split(";");
        for (String a : parts) {
            addAttribute(a, _class, ATRIBUTOS);
        }
    }

    public static void addAtributos(String _type, String _attributes, String _class) {
        String[] parts = _attributes.split(";");
        for (String a : parts) {
            addAttribute(_type + "@" + a, _class, ATRIBUTOS_TIPO_CLASE);
        }
    }

    public static ArrayList<String> getAtributos(String _class) {
        return getAttribute(_class, ATRIBUTOS);
    }

    public static ArrayList<String> getAtributosTC(String _class) {
        return getAttribute(_class, ATRIBUTOS_TIPO_CLASE);

    }

    public static ArrayList<String> getAllAtributos(String _class) {
        return getAllAttribute(_class, (e) -> getAllAtributos(e), (e) -> getAtributos(e));
    }

    public static void addHerencia(String _class, String _inheritance) {
        addAttribute(_inheritance, _class, HERENCIA);
    };

    public static ArrayList<String> getHerencia(String _class) {
        ArrayList<String> out = new ArrayList<>();

        if (containsComponent(_class, HERENCIA))
            for (String h : classes.get(_class).get(HERENCIA)) {
                out.add(h);
            }

        return out;
    };

    public static String chequeoAtributoSobreescrito(String _class) {

        Stack<String> hereda = new Stack<>();
        String error = null;

        for (String p : getHerencia(_class)) {
            hereda.push(p);
        }

        // En teoria no se permite la herencia multiple
        int nivel_herencia = 0;
        while (!hereda.isEmpty() && error == null) {

            String relative = hereda.pop();
            // Se puede hacer dado que solo tengo un nivel
            for (String family : getHerencia(relative)) {
                hereda.push(family);
            }

            if (containsMyAttributes(relative, _class)) {
                error = "No se puede sobreescribir atributos de clases heredadas " + convertFamily(nivel_herencia)
                        + ".";
            }
            nivel_herencia += 1;

        }

        return error;

    }

    private static String convertFamily(int i) {
        return switch (i) {
            case 0 -> "(padre)";
            case 1 -> "(abuelo)";
            default -> "";
        };
    }

    private static Tupla<String, String> acomodarString(String id, String instance) {
        instance = instance.replaceFirst("(?=@)", (id.startsWith(".") ? "" : ".") + id);
        String attribute = instance.replaceAll(".*:([^:]+)@.*", "$1");
        instance = instance.replaceAll(":([^@]+)@", "@");

        // System.out.println("El id " + instance + " el atributo " + attribute);

        return new Tupla<String, String>(attribute, instance);
    };

    private static void addMetodosInstancia(ArrayList<String> methods, String instancia) {
        for (String attribute : methods) {
            Tupla<String, String> out = acomodarString(attribute, instancia);
            TablaSimbolos.addFunction(out.getSecond());
            TablaSimbolos.addParameter(out.getSecond(), out.getFirst());
        }
    }

    private static void addAtributosInstancia(ArrayList<String> attributes, String _instance) {
        for (String attribute : attributes) {
            Tupla<String, String> out = acomodarString(attribute, _instance);
            TablaSimbolos.addIdentificador(out.getSecond());
            TablaSimbolos.addTipo(out.getFirst(), out.getSecond());
        }

    }

    public static void addInstancia(String _class, String variable_declarators) {

        String[] instancias = variable_declarators.split(";");

        for (String instancia : instancias) {
            // System.out.println("La instancia " + instancia + " de la clase " + _class);
            addAtributosInstancia(getAllAtributos(_class), instancia);
            // addMetodosInstancia(getAllMetodos(_class), instancia); los metodos no van, se
            // copia cada metodo y este deberia ser unico
        }
    }

    public static boolean esUnMetodoConcreto(String _method, String _class) {
        ArrayList<String> methodsToImplemented = getAllMetodos(_class);
        acomodarLista(methodsToImplemented);

        // System.out.println(methodsToImplemented);
        // System.out.println("metodo que se intenta implementar " + _method);

        return methodsToImplemented.contains(_method);
    }

    public static boolean esUnMetodoAImplementar(String _method, String _class) {
        ArrayList<String> methodsToImplemented = getAllMetodosIMPL(_class);
        acomodarLista(methodsToImplemented);

        // System.out.println(methodsToImplemented);
        // System.out.println("metodo que se intenta implementar " + _method);

        return methodsToImplemented.contains(_method);
    }

    // public static void printTable() {

    // out += "CLASES: ";
    // out += TablaClases.t.toString() + "\n";
    // out += "METODOS: ";
    // out += TablaClases.m.toString() + "\n";
    // out += "ATRIBUTOS: ";
    // out += TablaClases.a.toString() + "\n";
    // out += "INTERFACES: ";
    // out += TablaClases.i.toString();
    // }

    public static String printTable() { // Lo tengo que arreglar
        String out = "";

        int maxLengthFC = String.valueOf(classes.size()).length();
        int maxLengthSC = 0;

        if (!classes.isEmpty()) {
            System.out.println(">>>    LISTA DE CLASES");

            for (Map.Entry<String, HashMap<String, ArrayList<String>>> css : classes.entrySet()) {

                String title = "Clase " + css.getKey();
                HashMap<String, ArrayList<String>> body = css.getValue();

                String formatFC = "| %-" + maxLengthFC + "s.";
                String formatSC = " %-" + maxLengthSC + "s |";

                int titleLength = title.length();
                int totalLength = maxLengthFC + maxLengthSC + 4;
                int leftPadding = maxLengthFC;
                int rightPadding = totalLength - leftPadding;

                String border = "+" + "-".repeat(totalLength) + "+";

                System.out.println(border);
                System.out.println("|" + " ".repeat(leftPadding) + title + " ".repeat(rightPadding) + "|");
                System.out.println(border);

                for (int i = 0; i < body.size(); i++) {
                    String formattedRule = String.format(formatFC, i);
                    formattedRule += String.format(formatSC, body.get(i));
                    System.out.println(formattedRule);
                    if (i == body.size() - 1) {
                        out += border + '\n';
                    }
                }
            }

        } else
            System.out.println("No se generaron clases.");

        return out;
    }

}
