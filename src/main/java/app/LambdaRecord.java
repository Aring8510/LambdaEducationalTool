package app;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaRecord {

    String type;
    List<String> argName;
    List<String> argType;
    String returnType;
    MyPosition myPosition;

    LambdaRecord(String _type, List<String> _argName, List<String> _argType, String _returnType, MyPosition _myPosition){
        this.type = _type;
        this.argName = new ArrayList<>(_argName);
        this.argType = new ArrayList<>(_argType);
        this.returnType = _returnType;
        this.myPosition = new MyPosition(_myPosition);
    }

    public void describe(){
        System.out.println("Type -> " + this.type);
        System.out.print  ("Arg Name -> ");argName.forEach(an->System.out.println(an+" "));
        System.out.print  ("Arg Type -> ");argType.forEach(at->System.out.println(at+" "));
        System.out.println("Return Type -> " + returnType);
        System.out.println("myPosition -> (begin=" + myPosition.begin + ", end=" + myPosition.end + ", beginLine=" + myPosition.beginLine + ", endLine="+myPosition.endLine);
    }
}

