package compiladoresM2;

public class SemanticTableHelper {

    public static int changeTypeStringToInt(String type){
        switch (type){
            case "int":
                return 0;
            case "float":
                return 1;
            case "char":
                return 2;
            case "string":
                return 3;
            case "boolean":
                return 4;
            default:
                return -1;
        }
    }
    public static int changeOpsToInt(String ops){
        switch (ops){
            case "+":
                return 0;
            case "-":
                return 1;
            case "*":
                return 2;
            case "/":
                return 3;
            default:
                return 4;
        }
    }
}
