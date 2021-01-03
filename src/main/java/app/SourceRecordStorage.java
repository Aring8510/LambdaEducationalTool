package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceRecordStorage {
    // TODO:こういう変数は次からはOptionalにしろよな^^
    List<LambdaRecord> lambdaRecords = new ArrayList<>();
    List<MethodRecord> methodRecords = new ArrayList<>();
    // List<Record> mergedRecords = new ArrayList<>();

    private List<String> pTitle = new ArrayList<>();
    private List<String> pType = new ArrayList<>();
    private List<String> pUrl = new ArrayList<>();
    private List<String> pArgType = new ArrayList<>();
    private List<String> pRetType = new ArrayList<>();
    private List<String> pImgUrl = new ArrayList<>();

    boolean registerLambdaRecord(LambdaRecord lr){
        lambdaRecords.add(lr);
        // TODO:registerできなかったらfalse
        return true;
    }

    boolean registerMethodRecord(MethodRecord mr){
        methodRecords.add(mr);
        // TODO:registerできなかったらfalse
        return true;
    }
    // TODO:sort by mp
    // void sortRecordsByMyPosition(){
    // }

    void parseLambdaRecord(){
        // TODO:aho no blocker
        if( lambdaRecords == null ){ return;}
        lambdaRecords.forEach(lr-> {
            // TODO:数字を表示
            pTitle.add("ラムダ式:");
            pType.add(lr.type);
            // TODO:!!
            pUrl.add(lr.type);
            // TODO:!!
            if(!lr.argType.isEmpty()){
                pArgType.add(lr.argType.get(0));
            } else {
                pArgType.add("");
            }
            pRetType.add(lr.returnType);
            pImgUrl.add("./img/i1.png");
        });
    }

    void parseMethodRecord(){
        // TODO:aho no blocker
        if( methodRecords == null ){ return;}
        methodRecords.forEach(mr-> {
            // TODO:数字を表示
            pTitle.add("メソッド:" + mr.apiName);
            pType.add(mr.className);
            // TODO:!!
            pUrl.add(mr.className);
            // TODO:!!
            if(!mr.argumentTypeName.isEmpty()) {
                pArgType.add(mr.argumentTypeName.get(0));
            } else {
                pArgType.add("");
            }
            pRetType.add(mr.returnTypeName);
            pImgUrl.add("./img/i1.png");
        });
    }

    void describe(){
        System.out.println("methodRecords:");
        methodRecords.forEach( mr -> System.out.println("methodRecord:"+mr.methodName));
        System.out.println("lambdaRecords:");
        lambdaRecords.forEach( lr -> System.out.println("methodRecord:"+lr.type));
    }

    // void parseMergedRecords(){}


    public List getPTitle(){ return pTitle; }
    public void setPTitle(List p){ this.pTitle = new ArrayList(p);}
    public List getPType(){ return pType; }
    public void setPType(List p){ this.pType = new ArrayList(p);}
    public List getPUrl(){ return pUrl; }
    public void setPUrl(List p){ this.pUrl = new ArrayList(p);}
    public List getPArgType(){ return pArgType; }
    public void setPArgType(List p){ this.pArgType = new ArrayList(p);}
    public List getPRetType(){ return pRetType; }
    public void setPRetType(List p){ this.pRetType = new ArrayList(p);}
    public List getPImgUrl(){ return pImgUrl; }
    public void setPImgType(List p){ this.pImgUrl = new ArrayList(p);}
}