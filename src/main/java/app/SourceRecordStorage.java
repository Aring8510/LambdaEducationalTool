package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceRecordStorage {
    Map<MyPosition,LambdaRecord> LambdaRecords = new HashMap<>();
    Map<MyPosition,MethodRecord> MethodRecords = new HashMap<>();

    boolean registerLambdaRecord(LambdaRecord lr){
        return true;
    }
    boolean registerMethodRecords(MethodRecord mr){
        return true;
    }
}
