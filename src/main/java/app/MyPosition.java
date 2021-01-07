package app;

import java.sql.SQLOutput;
import java.util.Objects;

public class MyPosition {
    int begin, end, beginLine, endLine;

    MyPosition(int b, int e, int bl, int el) {
        begin = b;
        end = e;
        beginLine = bl;
        endLine = el;
    }

    MyPosition(MyPosition myPosition) {
        this.begin = myPosition.begin;
        this.end = myPosition.end;
        this.beginLine = myPosition.beginLine;
        this.endLine = myPosition.endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o instanceof MyPosition){
            MyPosition mp = (MyPosition) o;
            return (mp.begin == this.begin) && (mp.end == this.end)
                && (mp.beginLine == this.beginLine) && (mp.endLine == this.endLine);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hash(begin, end, beginLine, endLine);
    }

    // TODO:
    public int compareTo(MyPosition other){
        return 0;
    }

    public boolean isStart(int l, int c){
        return (beginLine == l) && (begin == c);
    }
    public boolean isEnd(int l, int c){
        return (endLine== l) && (end == c);
    }
    // public boolean contains(MyPosition mp){
    // return true;
    // }
    public void describe(){
        System.out.println("myPosition:(begin (line, column)), (end (line, column))");
        System.out.println("((" + beginLine + "," + begin + ")), ((" + endLine + "," + end + "))");
    }

}
