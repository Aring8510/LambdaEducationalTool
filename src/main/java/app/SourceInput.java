package app;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SourceInput {

    private String source;
    private String highlightedSource;
    static Map<MyPosition, Integer> colorMap = new HashMap<>();
    // ハイライトされたコード行のリスト
    private List<CodeLine> codeLines = new ArrayList<>();

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHighlightedSource() {
        return highlightedSource;
    }

    public void setHighlightedSource(String highlightedSource) {
        this.highlightedSource = highlightedSource;
    }

    public String createHighlightedSource(SourceRecordStorage srs) {
        List<String> sources = new ArrayList<>();
        BufferedReader br = new BufferedReader(new StringReader(source));
        br.lines().forEach(sources::add);
        // TODO: refactor me
        int i = 1;
        for (String s : sources) {
            codeLines.add(new CodeLine(s, i++, srs));
        }
        StringBuilder sb = new StringBuilder();
        codeLines.forEach(cl -> {
            cl.codeChars.forEach(cc -> {
                for(int j=0;j<cc.hasCloseTag;j++){
                    sb.append("</span>");
                }
                if (cc.hasAdditionalClass) {
                    sb.append("<span class='").append(cc.className).append("'>");
                }
                sb.append(escapeHTML(cc.ch));
            });
            sb.append("\n");
        });
        List<String> colorCounter = srs.getColorCounter();
        srs.pRecords.forEach(r-> {
            colorCounter.add(String.valueOf(colorMap.get(r.getMyPosition())));
        });
        return this.highlightedSource = new String(sb);
    }

    String escapeHTML(char ch) {
        switch (ch) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&#x27;";
            case '`':
                return "&#x60;";
            default:
                return String.valueOf(ch);
        }
    }
}

class CodeLine {
    public List<CodeChar> codeChars = new ArrayList<>();

    CodeLine(String line, final int lineNum, SourceRecordStorage srs) {
        List<Character> chs = line.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        int c = 1;
        for (char ch : chs) {
            codeChars.add(new CodeChar(ch, lineNum, c++, srs));
        }
    }

    public List<CodeChar> getCodeChars() {
        return codeChars;
    }

    public void setCodeChars(List<CodeChar> codeChars) {
        this.codeChars = codeChars;
    }
}
// source一文字づつを格納
// ラムダとメソッドがそれぞれ一つづつしか始点および終点にはならないものとする
// 重複した場合、後のRecordで上書かれる
// それぞれのタグがまたいだら、当然ぶっ壊れる

class CodeChar {
    public char ch;
    boolean hasAdditionalClass = false;
    int hasCloseTag = 0;
    String className = "ch ";
    int color = 0;

    // TODO:refactor me
    //  ネストやば
    CodeChar(Character c, final int line, final int column, SourceRecordStorage srs) {
        ch = c;
        if (srs.positionMap.isEmpty()) {
            return;
        }
        srs.positionMap.forEach((mp, records) -> {
            if (mp.isStart(line, column)) {
                records.forEach(r -> {
                    if (r.isLambdaRecord() || r.isMethodRecord()) {
                        if (hasAdditionalClass) {
                            SourceInput.colorMap.put(mp, color);
                        } else {
                            hasAdditionalClass = true;
                            className += "t_" + r.getCounter() + " ";
                            SourceInput.colorMap.put(mp, r.getCounter());
                            color = r.getCounter();
                        }
                    }
                });
            } else if (mp.isEnd(line, column - 1)) {
                records.forEach(r -> {
                    if (r.isLambdaRecord() || r.isMethodRecord()) {
                        hasCloseTag++;
                    }
                });
            }
        });
        if (hasAdditionalClass) {
            className += "add";
        }
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isHasAdditionalClass() {
        return hasAdditionalClass;
    }

    public void setHasAdditionalClass(boolean hasAdditionalClass) {
        this.hasAdditionalClass = hasAdditionalClass;
    }

    void test() {



        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.remove("a");
        System.out.println(list.get(0));


        Predicate<Integer> p = i -> (i==1);






    }
}
