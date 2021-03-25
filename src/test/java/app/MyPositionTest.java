package app;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyPositionTest {
    @Test
    public void testEquals() {
        MyPosition mp = new MyPosition(0, 1, 2, 3);
        boolean result = mp.equals(new MyPosition(0, 1, 2, 2));
        // リファクタリングするな！
        assertEquals(result, false);
    }

    @Test
    public void testIsStart() {
        MyPosition mp = new MyPosition(0, 1, 2, 3);
        assertAll(
                () -> assertTrue(mp.isStart(2, 0)),
                () -> assertFalse(mp.isStart(-1, 0)),
                () -> assertFalse(mp.isStart(2, -1)),
                () -> assertFalse(mp.isStart(-1, -1))
        );
    }

    @Test
    public void testCompareTo() {
        // TODO:もっとうまいやり方ないの？
        MyPosition mp0 = new MyPosition(1,3,0,2);
        MyPosition mp1 = new MyPosition(0,3,1,2);
        MyPosition mp2 = new MyPosition(1,3,1,1);
        MyPosition mp3 = new MyPosition(1,2,1,2);
        MyPosition mp4 = new MyPosition(1,3,1,2); //これが基準
        MyPosition mp5 = new MyPosition(1,4,1,2);
        MyPosition mp6 = new MyPosition(1,3,1,3);
        MyPosition mp7 = new MyPosition(2,3,1,2);
        MyPosition mp8 = new MyPosition(1,3,2,2);

        List<MyPosition> list1 = Arrays.asList(mp0,mp1,mp2,mp3,mp4,mp5,mp6,mp7,mp8);
        List<MyPosition> list2 = Arrays.asList(mp3,mp2,mp1,mp4,mp6,mp5,mp7,mp0,mp8);
        list2.sort(MyPosition::compareTo);
        // list2.forEach(MyPosition::describe);
        assertEquals(list1, list2);
    }
}