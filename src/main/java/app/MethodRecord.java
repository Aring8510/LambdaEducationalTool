package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class MethodRecord {

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
        List <String> temp = Arrays.asList(qualifiedSignature.split("\\."));
        StringBuilder sb = new StringBuilder();
        for (String t : temp){
            if(t.indexOf("(") == -1){ // TODO:ちょっとあやしいぞ
                sb.append(t+".");
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
