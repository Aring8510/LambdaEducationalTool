package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodRecord implements Record, Comparable<Record> {

    String methodName;
    MyPosition myPosition;
    String className;
    List<String> argumentTypeName;
    String returnTypeName;
    String apiName;
    int counter;

    public enum functionalAPI {
        notFunctionalAPI,
        StreamAPI,
        OptionalAPI
    }
    // TODO:変数でなく関数でよい?

    MethodRecord(String _methodName, MyPosition _myPosition, String qualifiedName, List<String> _argumentTypeName, String _returnTypeName) {
        this.methodName = _methodName;
        this.myPosition = new MyPosition(_myPosition);
        // qualifiedName:クラス名.メソッド名
        switch (MethodRecord.whichFunctionalAPI(qualifiedName)) {
            case StreamAPI:
                this.apiName = "Stream";
                break;
            case OptionalAPI:
                this.apiName = "Optional";
                break;
            case notFunctionalAPI:
                this.apiName = "notFunctionalAPI";
                break;
            default:
                this.apiName = "err in method record";
        }

        // classNameはqualifiedNameからメソッド名を落としたもの
        // qualifiedNameから最後の".hogefuga"を削除したものを使う
        this.className = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        this.argumentTypeName = new ArrayList<>(_argumentTypeName);
        this.returnTypeName = _returnTypeName;

        describe();
    }

    @Override
    public boolean isLambdaRecord() {
        return false;
    }

    @Override
    public boolean isMethodRecord() {
        return true;
    }

    @Override
    public MethodRecord asMethodRecord() {
        return this;
    }

    @Override
    public LambdaRecord asLambdaRecord() {
        System.out.println("this is MethodRecord");
        return null;
    }

    @Override
    public MyPosition getMyPosition() {
        return myPosition;
    }

    @Override
    public void describe() {
        System.out.println("methodName -> " + methodName);
        System.out.println("myPosition -> (begin=" + myPosition.beginColumn + ", end=" + myPosition.endColumn + ", beginLine=" + myPosition.beginLine + ", endLine=" + myPosition.endLine);
        System.out.println("className -> " + className);
        System.out.print("argumentTypeName -> ");
        argumentTypeName.forEach(atn -> System.out.print(atn + " "));
        System.out.println("\nreturnTypeName -> " + returnTypeName);
        String f = whichFunctionalAPI(className) == functionalAPI.OptionalAPI ? "OptionalAPI" : whichFunctionalAPI(className) == functionalAPI.StreamAPI ? "StreamAPI" : "notFunctionalAPI";
        System.out.println("whichFunctionalAPI -> " + f);
        System.out.println("isJavaAPI -> " + isJavaAPI(className));
    }

    // :TODO
    @Override
    public int compareTo(Record r) {
        return this.myPosition.compareTo(r.getMyPosition());
    }

    // 外部からも使うよ
    // 入力がqualifiedSignatureを期待しているのでだいぶクソ実装だが...
    public static boolean isJavaAPI(String classStr) {
        List<String> beginStr = Arrays.asList(classStr.split("\\."));
        return (!beginStr.isEmpty() && beginStr.get(0).equals("java")); // TODO:ほんとか?
    }

    // 入力がqualifiedSignatureを期待しているのでだいぶクソ実装だが...
    public static functionalAPI whichFunctionalAPI(String classStr) {
        if (classStr.indexOf("java.util.stream") == 0) {
            return functionalAPI.StreamAPI;
        } else if (classStr.indexOf("java.util.Optional") == 0) {
            return functionalAPI.OptionalAPI;
        } else {
            return functionalAPI.notFunctionalAPI;
        }
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
