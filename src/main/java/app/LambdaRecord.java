package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaRecord implements Record, Comparable<Record> {

    String Name;
    String type;
    List<String> argName;
    List<String> argType;
    String returnType;
    MyPosition myPosition;
    int counter;

    LambdaRecord(String _type, List<String> _argName, List<String> _argType, String _returnType, MyPosition _myPosition) {
        this.type = _type;
        this.argName = new ArrayList<>(_argName);
        this.argType = new ArrayList<>(_argType);
        this.returnType = _returnType;
        this.myPosition = new MyPosition(_myPosition);

        if (_argType.isEmpty()) {
            String ret_type = findParameterType(_type);
            System.out.println(_type + " => " + ret_type);
            this.argType.addAll(Arrays.asList(ret_type.split(",")));
        }
        if (_returnType == null) {
            this.returnType = findReturnType(_type);
            if (this.returnType == null) this.returnType = "UNABLE TO FIND";
        }
    }

    public static void test_find() {
        String[] test_strs = {
                "BiConsumer<T,U>",
                "BiFunction<T,U,R>",
                "BinaryOperator<T>",
                "BiPredicate<T,U>",
                "BooleanSupplier",
                "Consumer<T>",
                "DoubleBinaryOperator",
                "DoubleConsumer",
                "DoubleFunction<R>",
                "DoublePredicate",
                "DoubleSupplier",
                "DoubleToIntFunction",
                "DoubleToLongFunction",
                "DoubleUnaryOperator",
                "Function<T,R>",
                "IntBinaryOperator",
                "IntConsumer",
                "IntFunction<R>",
                "IntPredicate",
                "IntSupplier",
                "IntToDoubleFunction",
                "IntToLongFunction",
                "IntUnaryOperator",
                "LongBinaryOperator",
                "LongConsumer",
                "LongFunction<R>",
                "LongPredicate",
                "LongSupplier",
                "LongToDoubleFunction",
                "LongToIntFunction",
                "LongUnaryOperator",
                "ObjDoubleConsumer<T>",
                "ObjIntConsumer<T>",
                "ObjLongConsumer<T>",
                "Predicate<T>",
                "Supplier<T>",
                "ToDoubleBiFunction<T,U>",
                "ToDoubleFunction<T>",
                "ToIntBiFunction<T,U>",
                "ToIntFunction<T>",
                "ToLongBiFunction<T,U>",
                "ToLongFunction<T>",
                "UnaryOperator<T>"
        };
        for (String s: test_strs) {
            System.out.printf("%s args:\"%s\", ret:\"%s\"\n", s, findParameterType(s), findReturnType(s));
        }
    }

    // functional interfaceのclass名を取り出す
    static String IFuncToClassName(String lambdaType) {
        return lambdaType.replaceAll("java\\.util\\.function\\.", "");
    }

    public static String findParameterType(String lambdaType) {
        String type = IFuncToClassName(lambdaType);

        // functional interfaceは決まった数しかないので全部書いとけ
        // 2引数の場合はカンマ区切りで返す

        if (type.contains("Consumer")) {
            if (type.startsWith("Double")) return "double";
            else if (type.startsWith("Int")) return "int";
            else if (type.startsWith("Long")) return "long";
            else {
                String type_param = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
                if (type.startsWith("ObjDoubleConsumer")) {
                    return type_param + ",double";
                } else if (type.startsWith("ObjIntConsumer")) {
                    return type_param + ",int";
                } else if (type.startsWith("ObjLongConsumer")) {
                    return type_param + ",long";
                }

                // Consumer<T> & BiConsumer<T,U>
                return type_param;
            }
        }

        if (type.contains("Function")) {
            if (type.startsWith("Double")) return "double";
            else if (type.startsWith("Int")) return "int";
            else if (type.startsWith("Long")) return "long";
            else {
                String type_param = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
                if (type.startsWith("To")) return type_param;

                /* 引数の型だけを取り出す */
                // TODO: 型引数内に Function<Long, Long> みたいなやつがあると多分壊れる
                String[] ps = type_param.split(",");

                switch (ps.length) {
                    case 1:
                    case 2:
                        return ps[0];
                    case 3:
                        return ps[0] + "," + ps[1];
                }
            }
        }

        if (type.contains("Operator")) {
            String op_type;
            if (type.startsWith("Double")) op_type = "double";
            else if (type.startsWith("Int")) op_type = "int";
            else if (type.startsWith("Long")) op_type = "long";
            else { // UnaryOperator<T> or BinaryOperator<T>
                op_type = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
            }

            if (type.contains("BinaryOperator")) {
                return op_type + "," + op_type;
            }
            return op_type;
        }

        if (type.contains("Predicate")) {
            if (type.startsWith("Double")) return "double";
            else if (type.startsWith("Int")) return "int";
            else if (type.startsWith("Long")) return "long";
            else {
                return type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
            }
        }

        if (type.contains("Supplier")) {
            //FIXME: 引数ないときにvoidって言うか?
            return "void";
        }

        // not a functional interface
        return null;
    }

    public static String findReturnType(String lambdaType) {
        String type = IFuncToClassName(lambdaType);
        if (type.contains("Consumer")) {
            return "void";
        }

        if (type.contains("Function")) {
            if (type.contains("ToDouble")) return "double";
            else if (type.contains("ToInt")) return "int";
            else if (type.contains("ToLong")) return "long";
            else {
                String type_param = type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
                // TODO: 型引数内に Function<Long, Long> みたいなやつがあると多分壊れる
                String[] ps = type_param.split(",");

                // 最後の型引数が戻り値なので取り出す
                return ps[ps.length - 1];
            }
        }

        if (type.contains("Operator") || type.contains("Supplier")) {
            if (type.startsWith("Double")) return "double";
            else if (type.startsWith("Int")) return "int";
            else if (type.startsWith("Long")) return "long";
            else if (type.startsWith("Boolean")) return "boolean";
            else {
                // UnaryOperator<T> or BinaryOperator<T>
                // Or Supplier<T>
                return type.substring(type.indexOf('<') + 1, type.lastIndexOf('>'));
            }
        }

        if (type.contains("Predicate")) {
            return "boolean";
        }

        // not a functional interface
        return null;
    }

    @Override
    public boolean isLambdaRecord() {
        return true;
    }

    @Override
    public boolean isMethodRecord() {
        return false;
    }

    @Override
    public LambdaRecord asLambdaRecord() {
        return this;
    }

    @Override
    public MethodRecord asMethodRecord() {
        System.out.println("this is LambdaRecord");
        return null;
    }

    @Override
    public MyPosition getMyPosition() {
        return myPosition;
    }

    @Override
    public void describe() {
        System.out.println("Type -> " + this.type);
        System.out.print("Arg Name -> ");
        argName.forEach(an -> System.out.println(an + " "));
        System.out.print("Arg Type -> ");
        argType.forEach(at -> System.out.println(at + " "));
        System.out.println("Return Type -> " + returnType);
        System.out.println("myPosition -> (begin=" + myPosition.begin + ", end=" + myPosition.end + ", beginLine=" + myPosition.beginLine + ", endLine=" + myPosition.endLine);
    }

    // :TODO
    @Override
    public int compareTo(Record r) {
        return 0;
    }

    @Override
    public int getCounter() {
        return counter;
    }
}

