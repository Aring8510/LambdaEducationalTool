package app;
import java.util.*;

public class Test {
    public static void main(String... args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 3);
        // 各要素を2倍し標準出力する
        list.stream().map(i -> i * 2).forEach(i-> System.out.println(i));
        // 各要素の1のみを標準出力する
        System.out.println(list.stream().filter(i -> i == 1).count());

        list.stream().distinct().forEach(i-> System.out.println(i));
        list.stream().limit(2).forEach(i-> System.out.println(i));
    }
}