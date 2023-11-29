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
    public static final String ATTRIBUTE_SEPARATOR = ".";
    public static final String REF_SEPARATOR = "_";

    private interface Lambda {
        ArrayList<String> invoke(String _class);
    }

    private interface Gamma {
        ArrayList<String> invoke(String _class, boolean type);
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
            int j = method_c.indexOf(TYPE_SEPARATOR);
            int k = method_c.indexOf(REF_SEPARATOR);
            String method = method_c.substring(0, j);
            String parameter = method_c.substring(j + 1, k);
            String type = TablaSimbolos.getTypeLexema(parameter);

            methods.set(i, (type.isEmpty()) ? method + TYPE_SEPARATOR + parameter : method + TYPE_SEPARATOR + type);
        }

    };

    public static String implementaMetodosInterfaz(String _class, String _interface) {

        ArrayList<String> methodsToImplemented = getAllMetodosIMPL(_interface);
        ArrayList<String> methodsImplemented = getAllMetodos(_class);
        ArrayList<String> methodsPrototype = getAllMetodosIMPL(_class);

        // System.out.println("Metodos a implementar " + methodsToImplemented);
        // System.out.println("metodo implementados " + methodsImplemented);

        acomodarLista(methodsImplemented);
        acomodarLista(methodsToImplemented);
        acomodarLista(methodsPrototype);

        // System.out.println("Metodos a implementar acomodados " +
        // methodsToImplemented);
        // System.out.println("Metodos implementados acomodados" + methodsImplemented);
        // System.out.println("Metodos prototipos acomodados" + methodsPrototype);

        for (String method : methodsToImplemented) {
            if (methodsPrototype.contains(method))
                return "Se reconocio una CLASS que implementa una interface y NO reimplementa el metodo "
                        + method.substring(0, method.indexOf(TYPE_SEPARATOR)) + ".";

            if (!methodsImplemented.contains(method))
                return "Se reconocio una CLASS que implementa una interface y NO implementa el metodo "
                        + method.substring(0, method.indexOf(TYPE_SEPARATOR)) + ".";
        }

        return "";
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

    private static ArrayList<String> getAttributes(String _class, String _component) {
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

        String ref = _attribute + Scope.getScopeMain() + Scope.SEPARATOR + _class;
        String type = TablaSimbolos.getTypeLexema(ref);

        if (type == null || type.isEmpty()) {
            type = TablaSimbolos.getParameter(_attribute + Scope.getScopeMain() + Scope.SEPARATOR + _class);
            if (type == null)
                return "";
        }

        return TYPE_SEPARATOR + type + REF_SEPARATOR + ref;
    }

    private static ArrayList<String> getAllAttribute(String _class, Gamma f, Lambda g, boolean type) {
        ArrayList<String> out = new ArrayList<>();

        Stack<String> hereda = new Stack<>();

        for (String a : getAtributosTC(_class)) {
            String[] parts = a.split(Scope.SEPARATOR);

            ArrayList<String> TC_attributes = getAllAttribute(parts[0], f, g, type);

            for (String TC_attribute : TC_attributes) {
                out.add(parts[1] + ATTRIBUTE_SEPARATOR + TC_attribute
                        + ((type) ? getType(parts[1], TC_attribute, _class) : ""));
            }
        }

        for (String p : getHerencia(_class)) {
            hereda.push(p);
        }

        while (!hereda.isEmpty()) {

            String relative = hereda.pop();

            for (String family : getHerencia(relative))
                hereda.push(family);

            ArrayList<String> r_attributes = f.invoke(relative, type);

            for (String attribute : r_attributes) {
                out.add(relative + ATTRIBUTE_SEPARATOR + attribute
                        + ((type) ? getType(relative, attribute, _class) : ""));

            }

        }

        for (String attribute : g.invoke(_class)) {
            out.add(attribute + ((type) ? getType(_class, attribute) : ""));

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

    public static boolean containsMetodo(String _method, String _class) {
        return containsAttribute(_method, _class, METODOS);
    }

    public static ArrayList<String> getMetodos(String _class) {
        return getAttributes(_class, METODOS);
    }

    public static ArrayList<String> getAllMetodos(String _class) {
        return getAllAttribute(_class, (x, y) -> getAllMetodos(x, true), (e) -> getMetodos(e), true);
    }

    public static ArrayList<String> getAllMetodos(String _class, boolean type) {
        return getAllAttribute(_class, (x, y) -> getAllMetodos(x, y), (e) -> getMetodos(e), type);
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
        return getAttributes(_class, IMPL_FOR);
    }

    public static ArrayList<String> getAllMetodosIMPL(String _class) {
        return getAllAttribute(_class, (x, y) -> getAllMetodos(x, true), (e) -> getMetodoIMPL(e), true);
    }

    public static ArrayList<String> getAllMetodosIMPL(String _class, boolean type) {
        return getAllAttribute(_class, (x, y) -> getAllMetodos(x, type), (e) -> getMetodoIMPL(e), false);
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
        return getAttributes(_class, ATRIBUTOS);
    }

    public static boolean containsAtributo(String _attribute, String _class) {
        return containsAttribute(_attribute, _class, ATRIBUTOS);
    }

    public static void addAtributoTC(String _attribute, String _class) {
        addAttribute(_attribute, _class, ATRIBUTOS_TIPO_CLASE);
    }

    public static String getTypeAtributoTC(String _attribute, String _class) {

        for (String at : classes.get(_class).get(ATRIBUTOS_TIPO_CLASE)) {
            if (!_attribute.isEmpty() && at.contains(_attribute))
                return at.substring(0, at.indexOf(Scope.SEPARATOR));
        }

        return "";
    }

    public static ArrayList<String> getAtributosTC(String _class) {
        return getAttributes(_class, ATRIBUTOS_TIPO_CLASE);
    }

    public static ArrayList<String> getAllAtributos(String _class, boolean type) {
        return getAllAttribute(_class, (x, y) -> getAllAtributos(x, type), ((e) -> getAtributos(e)), type);
    }

    public static boolean containsAtributoTC(String _attribute, String _class) {
        return containsAttribute(_attribute, _class, ATRIBUTOS_TIPO_CLASE);
    }

    public static ArrayList<String> getAllAtributos(String _class) {
        return getAllAttribute(_class, (x, y) -> getAllAtributos(x, true), (e) -> getAtributos(e), true);
    }

    public static boolean tieneHerencia(String _class) {
        return classes.get(_class).containsKey(HERENCIA);

    }

    public static void addHerencia(String _class, String _inheritance) {
        addAttribute(_inheritance, _class, HERENCIA);
    };

    public static String getInstance(String s) {
        return s.substring(0, s.indexOf(ATTRIBUTE_SEPARATOR));
    }

    public static String searchMethod(String r, String scope) {
        String splitter = "\\" + ATTRIBUTE_SEPARATOR;
        String main = Scope.getScopeMain();

        String _instance = getInstance(r);

        System.out.println("Me quedo la clase " + _instance + " en el scope " + scope);

        // Clase principal
        String _class = TablaSimbolos.getTypeLexema(_instance + scope);

        // System.out.println(getAllAtributos(_class) + "funciona bien? busco sobre " +
        // _class);

        if (_class.isEmpty())
            return null;

        int index = r.indexOf(ATTRIBUTE_SEPARATOR);
        String _method = r.substring(index + 1);

        int lastIndex = _method.lastIndexOf(ATTRIBUTE_SEPARATOR);

        // System.out.println("la clase " + _class + " metodo " + _method);

        if (lastIndex != -1) {
            String part = _method.substring(0, lastIndex);
            _method = _method.substring(lastIndex + 1);

            // System.out.println("En el if tengo, la clase " + _class + " metodo " +
            // _method + " y la parte " + part);

            for (String _subClass : part.split(splitter)) {
                if (containsComponent(_class, ATRIBUTOS_TIPO_CLASE)) {
                    _class = getTypeAtributoTC(_subClass, _class);
                    // System.out.println("La sub clase es " + _class);
                } else if (containsHerencia(_subClass, _class)) {
                    _class = _subClass;
                } else
                    return null;
            }

        }

        // System.out.println(_method + main + Scope.SEPARATOR + _class);
        if (containsMetodo(_method, _class))
            return _instance + TYPE_SEPARATOR + _method + main + Scope.SEPARATOR + _class;

        return null;
    }

    public static ArrayList<String> getHerencia(String _class) {
        ArrayList<String> out = new ArrayList<>();

        if (containsComponent(_class, HERENCIA))
            for (String h : classes.get(_class).get(HERENCIA)) {
                out.add(h);
            }

        return out;
    };

    public static boolean containsHerencia(String _inheritance, String _class) {
        return containsAttribute(_inheritance, _class, HERENCIA);
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

    private static String getTipo(String type) {
        return type.substring(0, type.indexOf(REF_SEPARATOR));
    }

    private static String getRef(String ref) {
        return ref.substring(ref.indexOf(REF_SEPARATOR) + 1);
    }

    private static String getTipoYReferencia(String id) {
        return id.substring(id.indexOf(TYPE_SEPARATOR) + 1);
    }

    private static String getAtributoInstancia(String id, String instance) {

        id = id.substring(0, id.indexOf(TYPE_SEPARATOR));

        instance = instance.replaceFirst("(?=@)",
                (id.startsWith(ATTRIBUTE_SEPARATOR) ? "" : ATTRIBUTE_SEPARATOR) + id);
        instance = instance.replaceAll(":([^@]+)@", "@");

        // System.out.println("El id " + id + " y la instancia " + instance);

        return instance;
    };

    // private void addMetodosInstancia(ArrayList<String> methods, String
    // instancia23)
    // {
    // for (String attribute : methods) {
    // Tupla<String, String> out = acomodarString(attribute, instancia);
    // TablaSimbolos.addFunction(out.getSecond());
    // TablaSimbolos.addParameter(out.getSecond(), out.getFirst());
    // }
    // }

    private static void addAtributosInstancia(ArrayList<String> attributes, String _instance) {
        for (String attribute : attributes) {
            // System.out.println("Viene completito " + attribute);
            String id = getAtributoInstancia(attribute, _instance);
            String ref = getRef(getTipoYReferencia(attribute));
            String type = getTipo(getTipoYReferencia(attribute));

            TablaSimbolos.addIdentificador(id);
            TablaSimbolos.addTipo(type, id);
            TablaSimbolos.addRef(ref, id);
            TablaSimbolos.addUsedVariable(id);
        }

    }

    public static void addInstancia(String _class, String variable_declarators) {

        int i = variable_declarators.indexOf(Scope.SEPARATOR);
        String ambito = variable_declarators.substring(i);
        variable_declarators = variable_declarators.substring(0, i);
        String[] instancias = variable_declarators.split(";");

        // System.out.println("Las variables son " + variable_declarators);

        for (String instancia : instancias) {
            // System.out.println("La instancia " + instancia + " de la clase " + _class);

            addAtributosInstancia(getAllAtributos(_class), instancia + ambito);

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
