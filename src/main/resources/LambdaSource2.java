import java.util.function.*;
import java.util.*;

public class LambdaSource {
    void lambdaMethod1(Function<Integer, String> f) {
    }
    void lambdaMethod2() {
        int i = 1;
        String a = "a";
    }

    void test() {
        String str = "a.b.c.d";
        List<String> l = Arrays.asList(str.split("."));
        l.stream().forEach(s->System.out.println(s));
    }
    void test(Function<Integer, String> lamda) {

    }
    public static void main(String[] args){
        LambdaSource l = new LambdaSource();
        l.lambdaMethod1(i->String.valueOf(i));
    }
}