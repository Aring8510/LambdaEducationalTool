package app;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SourceInput{

    private String source;
    // ハイライトされたコード行のリスト
    private List<CodeLine> codeLines = new ArrayList<>();

    public String getSource() { return source; }
    public void setSource(String source) {
        this.source = source;
    }
    public List<CodeLine> getHighlightedSource() { return codeLines; }
    public void setHighlightedSource(List<CodeLine> source) {
        this.codeLines = new ArrayList<>(source);
    }

    public void describeHighlightedCode(){
        System.out.println("describe highlighted Code");
        codeLines.forEach(cl->{
            cl.codeChars.forEach(cc->{
                if(cc.isMethodStart){
                    System.out.print("<span class='"+cc.methodClassName + "'>");
                }
                if(cc.isLambdaStart){
                    System.out.print("<span class='"+cc.lambdaClassName+ "'>");
                }
                if(cc.isMethodEnd){
                    System.out.print("</span>");
                }
                if(cc.isLambdaEnd){
                    System.out.print("</span>");
                }
                System.out.print(cc.ch);
            });
            System.out.println("");
        });

    }

    public void createHighlightedSource(SourceRecordStorage srs){
        List<String> sources = new ArrayList<>();
        BufferedReader br = new BufferedReader(new StringReader(source));
        br.lines().forEach(sources::add);
        // TODO: refactor me
        int i = 1;
        for (String s : sources) {
            codeLines.add(new CodeLine(s, i++, srs));
        }
    }
}

class CodeLine{
    List<CodeChar> codeChars = new ArrayList<>();
    CodeLine(String line, final int lineNum, SourceRecordStorage srs){
        List<Character> chs = line.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        int c = 1;
        for(char ch : chs){
            codeChars.add(new CodeChar(ch, lineNum, c++, srs));
        }
    }
}
// source一文字づつを格納
// ラムダとメソッドがそれぞれ一つづつしか始点および終点にはならないものとする
// 重複した場合、後のRecordで上書かれる
// それぞれのタグがまたいだら、当然ぶっ壊れる

class CodeChar{
    public char ch;
    public boolean isLambdaStart, isMethodStart, isLambdaEnd, isMethodEnd;
    String lambdaClassName, methodClassName;
    // TODO:refactor me
    //  ネストやば
    CodeChar(Character c, final int line, final int column, SourceRecordStorage srs){
        ch = c;
        if(srs.positionMap.isEmpty()) { return; }
        srs.positionMap.forEach( (mp,records) ->{
            if(mp.isStart(line, column)){
                records.forEach(r->{
                    if(r.isLambdaRecord()){
                        isLambdaStart = true;
                        lambdaClassName = "l_" + r.getCounter();
                    }else if(r.isMethodRecord()){
                        isMethodStart = true;
                        methodClassName = "m_" + r.getCounter();
                    }
                });
            }else if(mp.isEnd(line, column-1)){
                records.forEach(r->{
                    if(r.isLambdaRecord()){
                        isLambdaEnd = true;
                    }else if(r.isMethodRecord()){
                        isMethodEnd = true;
                    }
                });
            }
        });
    }
}
