package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodRecord implements Record, Comparable<Record>{

    String methodName;
    MyPosition myPosition;
    String className;
    List<String> argumentTypeName;
    String returnTypeName;

    public enum functionalAPI{
        notFunctionalAPI,
        StreamAPI,
        OptionalAPI
    }
    // TODO:変数でなく関数でよい?

    MethodRecord(String _methodName, MyPosition _myPosition, String qualifiedSignature,  List<String> _argumentTypeName, String _returnTypeName){
        this.methodName = _methodName;
        this.myPosition = new MyPosition(_myPosition);
        // qualifiedSignature:クラス名.メソッド名(引数のクラス型)
        String[] temp = qualifiedSignature.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String t : temp){
            if(!t.contains("(")){ // TODO:ちょっとあやしいぞ
                sb.append(t).append(".");
            } else {
                break;
            }
        }
        this.className = sb.toString();
        // 最後のドットを消去
        this.className = this.className.substring(0, this.className.length() -1);
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
    public void describe(){
        System.out.println("methodName -> " + methodName);
        System.out.println("myPosition -> (begin=" + myPosition.begin + ", end=" + myPosition.end + ", beginLine=" + myPosition.beginLine + ", endLine="+myPosition.endLine);
        System.out.println("className -> " + className);
        System.out.print("argumentTypeName -> "); argumentTypeName.forEach(atn -> System.out.print(atn+" "));
        System.out.println("\nreturnTypeName -> " + returnTypeName);
        String f = whichFunctionalAPI(className) == functionalAPI.OptionalAPI ? "OptionalAPI" : whichFunctionalAPI(className) == functionalAPI.StreamAPI ? "StreamAPI" : "notFunctionalAPI";
        System.out.println("whichFunctionalAPI -> " + f);
        System.out.println("isJavaAPI -> " + isJavaAPI(className));
    }

    // :TODO
    @Override
    public int compareTo(Record r) {
        return 0;
    }

    // 外部からも使うよ
    public static boolean isJavaAPI(String classStr){
        List <String> beginStr = Arrays.asList(classStr.split("\\."));
        return (!beginStr.isEmpty() && beginStr.get(0).equals("java")); // TODO:ほんとか?
    }
    public static functionalAPI whichFunctionalAPI(String classStr){
        if(classStr.indexOf("java.util.stream") == 0){
            return functionalAPI.StreamAPI;
        } else if(classStr.indexOf("java.util.Optional") == 0){
            return functionalAPI.OptionalAPI;
        } else {
            return functionalAPI.notFunctionalAPI;
        }
    }
}
