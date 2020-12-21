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

import org.springframework.boot.*;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Some code that uses JavaParser.
 */
@SpringBootApplication
public class Main {

/*
    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

 */

    static SourceRecordStorage sourceRecordStorage = new SourceRecordStorage(); // parseしたソースコードの解析結果(レコード)を入れるクラス

    public static void main(String[] args) {

        // SpringApplication.run(Main.class, args);
        parse();


    }
    public static void parse() {
        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/resources"));

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        // reflectionTypeSolver.setParent(reflectionTypeSolver);
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File("src/main/resources"));

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(javaParserTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit c1;


        try{
             c1 = StaticJavaParser.parse(new File("C:\\Users\\nakam\\project\\Parser\\src\\main\\resources\\LambdaSource.java"));
            /*
            MethodCallExpr mce = Navigator.findNodeOfGivenClass(c1,MethodCallExpr.class);
            LambdaExpr le = Navigator.demandNodeOfGivenClass(c1, LambdaExpr.class);
            System.out.println(le.getParentNode().toString());
            System.out.println(le.toString());

            Log.info("methodName\n"+mce.getNameAsString());
            Log.info("methodArgType\n"+mce.resolve().getQualifiedSignature());
             */

            c1.accept(new ModifierVisitor<Void>() {
                @Override
                public Visitable visit(LambdaExpr le, Void arg) {
                    // le.getParameters().stream().forEach( p -> System.out.println( p.getName() + "->"+p.getType().toString()));
                    return super.visit(le, arg);
                }
                @Override
                public Visitable visit(MethodCallExpr mce, Void arg) {
                    System.out.println(">>>>>>>>>>  " + mce.getName()+" resolving... ");
                    ResolvedMethodDeclaration rmd = mce.resolve(); // 何故か標準出力しやがる
                    System.out.println(rmd.getQualifiedSignature() + "  finished! <<<<<<<<<<\n");

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
                        sourceRecordStorage.registerMethodRecords(methodRecord);
                    }



                    return super.visit(mce, arg);
                }
                @Override
                public Visitable visit(VariableDeclarator vd, Void arg) {
                    // System.out.println(vd.resolve().getName() + " type ->" + vd.resolve().getType());
                    // System.out.println(vd.getNameAsString());
                    // System.out.println(ResolvedLambdaConstraintType.bound(vd.resolve().getType()).describe());
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




        /*
        // Our sample is in the root of this directory, so no package name.
        // CompilationUnit cu = sourceRoot.parse("", "Blabla.java");
        CompilationUnit cu = sourceRoot.parse("", "LambdaSource.java");
        Log.info("Positivizing!");

        cu.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(LambdaExpr le, Void arg) {
                int line = le.getBegin().get().line;
                int column = le.getBegin().get().column;
                Log.info("$$$$$$ LamdaExpr detected in line" + line + " column "+ column);
                // Log.info("lamda expression type?:" + le.isObjectCreationExpr());

                Log.info("toString...");
                Log.info(le.toString());
                List<Parameter> nodeList = le.getParameters();
                Log.info("parameters...");
                List<String> parameterType = new ArrayList<String>();
                nodeList.stream().forEach(p -> Log.info("name:" + p.getNameAsString() + " type:" + (p.getType().isUnknownType() ? "Unknown Type" : p.getType().toString())));
                return super.visit(le, arg);
            }
            @Override
            public Visitable visit(MethodCallExpr mce, Void arg) {
                int line = mce.getBegin().get().line;
                int column = mce.getBegin().get().column;
                Log.info("###### method call detected in line" + line + " column "+ column);
                Log.info("name:"+mce.getNameAsString());
                Log.info("type:"+mce.getTypeArguments().toString());
                return super.visit(mce, arg);
            }
            @Override
            public Visitable visit(VariableDeclarator vd, Void arg) {
                int line = vd.getBegin().get().line;
                int column = vd.getBegin().get().column;
                Log.info("###### Variable Declaration detected in line" + line + " column "+ column);
                Log.info("name:"+vd.getNameAsString());
                Log.info("parameters...");
                Log.info(vd.getType().toString());
                Log.info("toString...");
                Log.info(vd.toString());
                return super.visit(vd, arg);
            }
        }, null);

        // This saves all the files we just read to an output directory.
        sourceRoot.saveAll(
                // The path of the Maven module/project which contains the LogicPositivizer class.
                CodeGenerationUtils.mavenModuleRoot(Main.class)
                        // appended with a path to "output"
                        .resolve(Paths.get("output")));
         */

    }
}
