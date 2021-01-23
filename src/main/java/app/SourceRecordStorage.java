package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class SourceRecordStorage {
    // TODO:こういう変数は次からはOptionalにしろよな^^
    List<LambdaRecord> lambdaRecords = new ArrayList<>();
    List<MethodRecord> methodRecords = new ArrayList<>();
    // List<Record> mergedRecords = new ArrayList<>();
    Map<MyPosition, List<Record>> positionMap = new HashMap<>();
    Map<String, String> imageUrlMap = new HashMap<>();

    int counter = 0;

    private List<String> pTitle = new ArrayList<>();
    private List<String> pType = new ArrayList<>();
    private List<String> pUrl = new ArrayList<>();
    private List<String> pArgType = new ArrayList<>();
    private List<String> pRetType = new ArrayList<>();
    private List<String> pImgUrl = new ArrayList<>();
    private List<Integer> colorCounter = new ArrayList<>();

    SourceRecordStorage(){
        // 用意している画像を登録
        imageUrlMap.put("distinct", "./img/distinct.png");
        imageUrlMap.put("filter",   "./img/filter.png");
        imageUrlMap.put("forEach",  "./img/forEach.png");
        imageUrlMap.put("limit",    "./img/limit.png");
        imageUrlMap.put("map",      "./img/map.png");
        imageUrlMap.put("Function", "./img/Function.png");
        imageUrlMap.put("Supplier", "./img/Supplier.png");
        imageUrlMap.put("Consumer", "./img/Consumer.png");
        imageUrlMap.put("Predicate","./img/Predicate.png");
        imageUrlMap.put("DoubleToIntFunction",  "./img/XXToYYFunction.png");
        imageUrlMap.put("DoubleToLongFunction", "./img/XXToYYFunction.png");
        imageUrlMap.put("IntToDoubleFunction",  "./img/XXToYYFunction.png");
        imageUrlMap.put("IntToLongFunction",    "./img/XXToYYFunction.png");
        imageUrlMap.put("LongToIntFunction",    "./img/XXToYYFunction.png");
        imageUrlMap.put("LongToDoubleFunction", "./img/XXToYYFunction.png");
    }

    boolean registerLambdaRecord(LambdaRecord lr) {
        lambdaRecords.add(lr);
        lr.counter = counter++;
        // TODO:refactor me
        positionMap.putIfAbsent(lr.myPosition, new ArrayList<>());
        positionMap.get(lr.myPosition).add(lr);
        // TODO:registerできなかったらfalse
        return true;
    }

    boolean registerMethodRecord(MethodRecord mr) {
        methodRecords.add(mr);
        mr.counter = counter++;
        // TODO:refactor me
        positionMap.putIfAbsent(mr.myPosition, new ArrayList<>());
        positionMap.get(mr.myPosition).add(mr);
        // TODO:registerできなかったらfalse
        return true;
    }
    // TODO:sort by mp
    // void sortRecordsByMyPosition(){
    // }

    void parseLambdaRecord() {
        // TODO:aho no blocker
        if (lambdaRecords == null) {
            return;
        }
        lambdaRecords.forEach(lr -> {
            pTitle.add("ラムダ式:"+lr.name);
            pType.add(lr.type);
            pUrl.add(lr.type);
            if (!lr.argType.isEmpty()) {
                pArgType.add(lr.argType.get(0));
            } else {
                pArgType.add("");
            }
            pRetType.add(lr.returnType);
            String iUrl = imageUrlMap.containsKey(lr.name) ? imageUrlMap.get(lr.name) : "./img/dummy.png";
            pImgUrl.add(iUrl);
        });
    }

    void parseMethodRecord() {
        // TODO:aho no blocker
        if (methodRecords == null) {
            return;
        }
        methodRecords.forEach(mr -> {
            pTitle.add(mr.apiName + "メソッド:" + mr.methodName);
            pType.add(mr.className);
            pUrl.add(mr.className);
            if (!mr.argumentTypeName.isEmpty()) {
                pArgType.add(mr.argumentTypeName.get(0));
            } else {
                pArgType.add("");
            }
            pRetType.add(mr.returnTypeName);
            String iUrl = imageUrlMap.containsKey(mr.methodName) ? imageUrlMap.get(mr.methodName) : "./img/dummy.png";
            pImgUrl.add(iUrl);
        });
    }

    void reCalculateColorCounter() { // メソッド・ラムダの色分け用のカウンターを計算する

    }

    void describe() {
        System.out.println("methodRecords:");
        methodRecords.forEach(mr -> System.out.println("methodRecord:" + mr.methodName));
        System.out.println("lambdaRecords:");
        lambdaRecords.forEach(lr -> System.out.println("methodRecord:" + lr.type));
    }
    // void parseMergedRecords(){}

    public List getPTitle() {
        return pTitle;
    }

    public void setPTitle(List p) {
        this.pTitle = new ArrayList(p);
    }

    public List getPType() {
        return pType;
    }

    public void setPType(List p) {
        this.pType = new ArrayList(p);
    }

    public List getPUrl() {
        return pUrl;
    }

    public void setPUrl(List p) {
        this.pUrl = new ArrayList(p);
    }

    public List getPArgType() {
        return pArgType;
    }

    public void setPArgType(List p) {
        this.pArgType = new ArrayList(p);
    }

    public List getPRetType() {
        return pRetType;
    }

    public void setPRetType(List p) {
        this.pRetType = new ArrayList(p);
    }

    public List getPImgUrl() {
        return pImgUrl;
    }

    public void setPImgType(List p) {
        this.pImgUrl = new ArrayList(p);
    }

    public List<Integer> getColorCounter() {
        return colorCounter;
    }

    public void setColorCounter(List<Integer> colorCounter) {
        this.colorCounter = colorCounter;
    }
}