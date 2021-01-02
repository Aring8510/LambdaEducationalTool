package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceRecordStorage {
    List<LambdaRecord> LambdaRecords = new ArrayList<>();
    List<MethodRecord> MethodRecords = new ArrayList<>();
    // List<Record> mergedRecords = new ArrayList<>();

    private List<String> pTitle;
    private List<String> pType = Arrays.asList("a", "b", "c");
    private List<String> pUrl = Arrays.asList("d", "e", "f");
    private List<String> pArgType;
    private List<String> pRetType;
    private List<String> pImgUrl;

    boolean registerLambdaRecord(LambdaRecord lr){
        LambdaRecords.add(lr);
        // TODO:registerできなかったらfalse
        return true;
    }

    boolean registerMethodRecords(MethodRecord mr){
        MethodRecords.add(mr);
        // TODO:registerできなかったらfalse
        return true;
    }
    // TODO:sort by mp
    // void sortRecordsByMyPosition(){
    // }

    void parseLambdaRecord(){
    }

    void parseMethodRecords(){
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