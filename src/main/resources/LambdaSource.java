import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;

public class LambdaSource {
    IntToDoubleFunction i = i -> (double) i;
    LongToDoubleFunction l = i -> (double) i;
    DoubleToIntFunction d = i -> (int) i;

    void lambdaMethod1(Function<Integer, String> f) {
    }

    void lambdaMethod2() {
        Function<Integer, String> func1 = i -> "1";
        Function<String, String> func2 = (String s) -> s;

        int i = 1;
        String a = "a";
        lambdaMethod1(func1);
    }

    void test() {
        String str = "a.b.c.d";
        List<String> l = Arrays.asList(str.split("."));
        l.stream().filter(s -> s.chars().allMatch(Character::isDigit)).forEach(s -> System.out.println(s));
    }

    void test(Function<Integer, String> lamda) {

    }

    public static void main(String[] args) {
        LambdaSource l = new LambdaSource();
        l.lambdaMethod1(i -> {
            return String.valueOf(i);
        });
    }
}

