package app;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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


        try {
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
                    System.out.println(">>>>>>>>>>  " + mce.getName() + " resolving... ");
                    ResolvedMethodDeclaration rmd = mce.resolve(); // 何故か標準出力しやがる
                    System.out.println(rmd.getQualifiedSignature() + "  finished! <<<<<<<<<<\n");

                    // MethodRecordの生成
                    if (MethodRecord.whichFunctionalAPI(rmd.getQualifiedSignature()) == MethodRecord.functionalAPI.OptionalAPI ||
                            MethodRecord.whichFunctionalAPI(rmd.getQualifiedSignature()) == MethodRecord.functionalAPI.StreamAPI) {
                        List<String> rmdArgType = new ArrayList<>();
                        for (int i = 0; i < rmd.getNumberOfParams(); i++) {
                            rmdArgType.add(rmd.getParam(i).describeType());
                        }

                        Position beginPosition = mce.getBegin().orElse(new Position(-1, -1));
                        Position endPosition = mce.getEnd().orElse(new Position(-1, -1));
                        MyPosition myPosition = new MyPosition(beginPosition.column, endPosition.column, beginPosition.line, endPosition.line);
                        MethodRecord methodRecord = new MethodRecord(rmd.getName(), myPosition,
                                rmd.getQualifiedName(), rmdArgType, rmd.getReturnType().describe());
                        sourceRecordStorage.registerMethodRecord(methodRecord);
                    }
                    // LambdaRecordの生成
                    // TODO:リファクタリング頼む
                    for (int i = 0; i < mce.getArguments().size(); i++) {
                        if (mce.getArguments().get(i).isLambdaExpr()) {
                            String lambdaType = rmd.getParam(i).getType().describe();
                            List<String> argTypes = new ArrayList<>();
                            List<String> argNames = new ArrayList<>();
                            mce.getArgument(i).ifLambdaExpr(le -> {
                                        le.getParameters().forEach(p -> {
                                            argNames.add(p.getNameAsString());
                                            // argTypes.add(p.getType().toString());
                                        });
                                        MyPosition mp = new MyPosition(createMyPositionByLe(le));
                                        // TODO:やばい
                                        LambdaRecord lr = new LambdaRecord(lambdaType, argNames, argTypes, null, mp);
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
                    // LambdaRecordの生成
                    // TODO:リファクタリング頼む

                    ResolvedValueDeclaration rvd = vd.resolve();
                    String lambdaType = rvd.getType().describe();
                    List<String> argTypes = new ArrayList<>();
                    List<String> argNames = new ArrayList<>();
                    vd.getInitializer().ifPresent(i -> i.ifLambdaExpr(le -> {
                        le.getParameters().forEach(p->{
                            argNames.add(p.getType().toString());
                        });
                        MyPosition mp = new MyPosition(createMyPositionByLe(le));
                        // TODO:やばい
                        LambdaRecord lr = new LambdaRecord(lambdaType, argNames, argTypes, null, mp);
                        lr.describe();
                        sourceRecordStorage.registerLambdaRecord(lr);
                    }));
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

                public MyPosition createMyPositionByLe(LambdaExpr le){
                    int b = le.getBegin().orElse(new Position(-1,-1)).column;
                    int e = le.getEnd().orElse(new Position(-1,-1)).column;
                    int bl = le.getBegin().orElse(new Position(-1,-1)).line;
                    int el = le.getEnd().orElse(new Position(-1,-1)).line;
                    return new MyPosition(b,e,bl,el);

                }
            }, null);

        } catch (Exception e) {
            Log.info("parse err");
            e.printStackTrace();
        }
        return sourceRecordStorage;
    }
}
