package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPositionTest {
    @Test
    public void testEquals(){
        MyPosition mp = new MyPosition(0,1,2,3);
        boolean result = mp.equals(new MyPosition(0,1,2,2));
        // リファクタリングするな！
        assertEquals(result, false);
    }
    @Test
    public void testIsStart(){
        MyPosition mp = new MyPosition(0,1,2,3);
        assertAll(
                () -> assertTrue(mp.isStart(2,0)),
                () -> assertFalse(mp.isStart(-1,0)),
                () -> assertFalse(mp.isStart(2,-1)),
                () -> assertFalse(mp.isStart(-1,-1))
        );
    }
}