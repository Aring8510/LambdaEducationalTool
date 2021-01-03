package app;

import app.storage.StorageService;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedLambdaConstraintType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import com.github.javaparser.symbolsolver.resolution.typesolvers.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javassist.expr.MethodCall;
import org.springframework.boot.*;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Some code that uses JavaParser.
 */
@SpringBootApplication
public class Main {


    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
        // parse();

    }
    public static SourceRecordStorage parse(String source) {
        SourceRecordStorage sourceRecordStorage = new SourceRecordStorage(); // parseしたソースコードの解析結果(レコード)を入れるクラス
        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/resources"));

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File("src/main/resources"));

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(javaParserTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit cu;


        try{
            // cu = StaticJavaParser.parse(new File("./src/main/resources/LambdaSource.java"));
            cu = StaticJavaParser.parse(source);

            /*
            MethodCallExpr mce = Navigator.findNodeOfGivenClass(c1,MethodCallExpr.class);
            LambdaExpr le = Navigator.demandNodeOfGivenClass(c1, LambdaExpr.class);
            System.out.println(le.getParentNode().toString());
            System.out.println(le.toString());

            Log.info("methodName\n"+mce.getNameAsString());
            Log.info("methodArgType\n"+mce.resolve().getQualifiedSignature());
             */

            cu.accept(new ModifierVisitor<Void>() {
                @Override
                public Visitable visit(LambdaExpr le, Void arg) {
                    /*
                    System.out.println(le.getParentNode().get().toString());
                     */
                    return super.visit(le, arg);

                }
                @Override
                public Visitable visit(MethodCallExpr mce, Void arg) {
                    System.out.println(">>>>>>>>>>  " + mce.getName()+" resolving... ");
                    ResolvedMethodDeclaration rmd = mce.resolve(); // 何故か標準出力しやがる
                    System.out.println(rmd.getQualifiedSignature() + "  finished! <<<<<<<<<<\n");

                    // MethodRecordの生成
                    if (MethodRecord.whichFunctionalAPI(rmd.getQualifiedSignature()) == MethodRecord.functionalAPI.OptionalAPI ||
                            MethodRecord.whichFunctionalAPI(rmd.getQualifiedSignature()) == MethodRecord.functionalAPI.StreamAPI) {
                        // ResolvedMethodDeclarationのgetTypeParameters()が壊れている?ので古のFor文を使う。
                        // mce.resolve().getTypeParameters().stream().forEach(..);
                        List<String> rmdArgType = new ArrayList<>();
                        for(int i=0;i<rmd.getNumberOfParams(); i++){
                            rmdArgType.add(rmd.getParam(i).describeType());
                        }
                        Position beginPosition = mce.getBegin().orElse(new Position(-1,-1));
                        Position endPosition = mce.getEnd().orElse(new Position(-1,-1));
                        MyPosition myPosition = new MyPosition(beginPosition.column, endPosition.column, beginPosition.line, endPosition.line);
                        MethodRecord methodRecord = new MethodRecord(rmd.getName(), myPosition,
                                rmd.getQualifiedSignature(), rmdArgType, rmd.getReturnType().describe());
                        sourceRecordStorage.registerMethodRecord(methodRecord);
                    }
                    // LambdaRecordの生成
                    // TODO:リファクタリング頼む
                    for(int i=0; i < mce.getArguments().size();i++){
                        if (mce.getArguments().get(i).isLambdaExpr()){
                            String lambdaType = rmd.getParam(i).getType().describe();
                            List<String> argTypes = new ArrayList<>();
                            List<String> argNames = new ArrayList<>();
                            mce.getArgument(i).ifLambdaExpr(le-> {
                                        le.getParameters().forEach(p->{
                                            argNames.add(p.getNameAsString());
                                            // argTypes.add(p.getType().toString());
                                        });
                                        int b = le.getBegin().orElse(new Position(-1,-1)).column;
                                        int e = le.getEnd().orElse(new Position(-1,-1)).column;
                                        int bl = le.getBegin().orElse(new Position(-1,-1)).line;
                                        int el = le.getEnd().orElse(new Position(-1,-1)).line;
                                        final MyPosition mp = new MyPosition(b,e,bl,el);
                                        // TODO:やばい
                                        LambdaRecord lr = new LambdaRecord(lambdaType, argNames, argTypes, "brabra", mp);
                                        lr.describe();
                                        sourceRecordStorage.registerLambdaRecord(lr);
                                    }
                            );

                        }
                    }
                    return super.visit(mce, arg);
                }
                @Override
                public Visitable visit(VariableDeclarator vd, Void arg) {
                    /*
                    System.out.println(vd.resolve().getName() + " type ->" + vd.resolve().getType());
                    System.out.println(vd.getNameAsString());
                    System.out.println(ResolvedLambdaConstraintType.bound(vd.resolve().getType()).describe());
                     */
                    return super.visit(vd, arg);
                }
                @Override
                public Visitable visit(FieldDeclaration fd, Void arg) {
                    // System.out.println(fd.resolve().getName());

                    return super.visit(fd, arg);
                }
                @Override
                public Visitable visit(ClassOrInterfaceDeclaration coifd, Void arg) {
                    // System.out.println(coifd.resolve().getName());
                    return super.visit(coifd, arg);
                }
                public Visitable visit(ClassOrInterfaceType coift, Void arg) {
                    // System.out.println(coift.resolve().getQualifiedName());
                    return super.visit(coift, arg);
                }
            }, null);

        }
        catch (Exception e){
            Log.info("parse err");
            e.printStackTrace();
        }
        return sourceRecordStorage;
    }
}
