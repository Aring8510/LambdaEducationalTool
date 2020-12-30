package app;

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

    public boolean equals(MyPosition mp) {
        return (mp.begin == this.begin) && (mp.end == this.end)
                && (mp.beginLine == this.beginLine) && (mp.endLine == this.endLine);
    }
    // public boolean contains(MyPosition mp){
    // return true;
    // }

}
