package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceRecordStorage {
    List<LambdaRecord> LambdaRecords = new ArrayList<>();
    List<MethodRecord> MethodRecords = new ArrayList<>();

    boolean registerLambdaRecord(LambdaRecord lr){
        LambdaRecords.add(lr);
        return true;
    }
    boolean registerMethodRecords(MethodRecord mr){
        MethodRecords.add(mr);
        return true;
    }
}
