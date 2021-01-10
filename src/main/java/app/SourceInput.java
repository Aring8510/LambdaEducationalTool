package app;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SourceInput{

    private String source;
    private String highlightedSource;
    // ハイライトされたコード行のリスト
    private List<CodeLine> codeLines = new ArrayList<>();

    public String getSource() { return source; }
    public void setSource(String source) {
        this.source = source;
    }
    public String getHighlightedSource() { return highlightedSource; }
    public void setHighlightedSource(String highlightedSource) {
        this.highlightedSource = highlightedSource;
    }

    public String createHighlightedSource(SourceRecordStorage srs){
        List<String> sources = new ArrayList<>();
        BufferedReader br = new BufferedReader(new StringReader(source));
        br.lines().forEach(sources::add);
        // TODO: refactor me
        int i = 1;
        for (String s : sources) {
            codeLines.add(new CodeLine(s, i++, srs));
        }
        StringBuilder sb = new StringBuilder();
        codeLines.forEach(cl->{
            cl.codeChars.forEach(cc->{
                if (cc.hasCloseTag){
                    sb.append("</span>");
                }
                if (cc.hasAdditionalClass){
                    sb.append("<span class='").append(cc.className).append("'>");
                }
                sb.append(escapeHTML(cc.ch));
            });
            sb.append("\n");
        });
        return this.highlightedSource = new String(sb);
    }

    String escapeHTML(char ch){
        switch (ch){
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

class CodeLine{
    public List<CodeChar> codeChars = new ArrayList<>();
    CodeLine(String line, final int lineNum, SourceRecordStorage srs){
        List<Character> chs = line.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        int c = 1;
        for(char ch : chs){
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

class CodeChar{
    public char ch;
    boolean hasAdditionalClass = false;
    boolean hasCloseTag = false;
    String className = "ch ";
    // TODO:refactor me
    //  ネストやば
    CodeChar(Character c, final int line, final int column, SourceRecordStorage srs){
        ch = c;
        if(srs.positionMap.isEmpty()) { return; }
        srs.positionMap.forEach( (mp,records) ->{
            if(mp.isStart(line, column)){
                records.forEach(r->{
                    if(r.isLambdaRecord()){
                        hasAdditionalClass = true;
                        className += "l_" + r.getCounter() + " ";
                    }
                    if(r.isMethodRecord()){
                        hasAdditionalClass = true;
                        className += "m_" + r.getCounter() + " ";
                    }
                });
            }else if(mp.isEnd(line, column-1)){
                records.forEach(r->{
                    if(r.isLambdaRecord()){
                        hasCloseTag = true;
                    }
                    if(r.isMethodRecord()){
                        hasCloseTag = true;
                    }
                });
            }
        });
        if(hasAdditionalClass){
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
}
