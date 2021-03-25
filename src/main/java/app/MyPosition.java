package app;

import java.util.Objects;

public class MyPosition implements Comparable<MyPosition>{
    int beginColumn, endColumn, beginLine, endLine;

    MyPosition(int beginColumn, int endColumn, int beginLine, int endLine) {
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.beginLine = beginLine;
        this.endLine = endLine;
    }

    MyPosition(MyPosition myPosition) {
        this.beginColumn = myPosition.beginColumn;
        this.endColumn = myPosition.endColumn;
        this.beginLine = myPosition.beginLine;
        this.endLine = myPosition.endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof MyPosition) {
            MyPosition mp = (MyPosition) o;
            return (mp.beginColumn == this.beginColumn) && (mp.endColumn == this.endColumn)
                    && (mp.beginLine == this.beginLine) && (mp.endLine == this.endLine);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginColumn, endColumn, beginLine, endLine);
    }

    // TODO:
    public int compareTo(MyPosition other) {
        if (this.beginLine > other.beginLine) {
            return 1;
        } else if (this.beginLine < other.beginLine) {
            return -1;
        } else { // 始まる行が同じ
            if (this.beginColumn > other.beginColumn) {
                return 1;
            } else if (this.beginColumn < other.beginColumn){
                return -1;
            } else {// 始まる行も列も同じ
                if (this.endLine > other.endLine) {
                    return 1;
                } else if (this.endLine < other.endLine) {
                    return -1;
                } else { // 始まる行も列も同じで、終わる行も同じ
                    return Integer.compare(this.endColumn, other.endColumn);
                }
            }
        }
    }

    public boolean isStart(int l, int c) {
        return (beginLine == l) && (beginColumn == c);
    }

    public boolean isEnd(int l, int c) {
        return (endLine == l) && (endColumn == c);
    }

    // public boolean contains(MyPosition mp){
    // return true;
    // }
    public void describe() {
        System.out.println("myPosition:(begin (line, column)), (end (line, column))");
        System.out.println("((" + beginLine + "," + beginColumn + ")), ((" + endLine + "," + endColumn + "))");
    }

}
