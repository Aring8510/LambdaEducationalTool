package app;
import java.util.*;
public class Test {
    public static void main(String... args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(-111,-222,-333,-444,-555));
        list.stream().filter(i -> i < -300).map(i -> String.valueOf(i*-1))
                .flatMap(s->s.chars().mapToObj(c -> (char)c)).forEach(s -> System.out.println(s));
    }
}