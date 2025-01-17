package app;

public interface Record {
    boolean isLambdaRecord();

    LambdaRecord asLambdaRecord();

    boolean isMethodRecord();

    MethodRecord asMethodRecord();

    MyPosition getMyPosition();

    int getCounter();

    void describe();
}
