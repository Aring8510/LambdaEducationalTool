public class LambdaSource {

    void lambdaMethod1(Function<Integer, String> f) {
    }

    void lambdaMethod2() {
        Function<Integer, String> func1 = i -> "1";
        Function<String, String> func2 = (String s) -> s;
        lambdaMethod1(func1);
    }

    void test() {
    }

    void test(Function<Integer, String> lamda) {
    }

    public static void main(String[] args) {
        LambdaSource l = new LambdaSource();
        l.lambdaMethod1(i -> String.valueOf(i));
    }
}
