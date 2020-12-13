package app;

import app.storage.StorageService;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import com.github.javaparser.symbolsolver.resolution.typesolvers.*;

import java.io.File;
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
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);


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

        try{
            CompilationUnit c1 = StaticJavaParser.parse(new File("C:\\Users\\nakam\\project\\Parser\\src\\main\\resources\\LambdaSource.java"));
            MethodCallExpr mce1 = Navigator.findNodeOfGivenClass(c1,MethodCallExpr.class);
            Log.info("methodName\n"+mce1.getNameAsString());
            Log.info("methodArgType\n"+mce1.resolve().getQualifiedSignature());
        }
        catch (Exception e){
            Log.info("parse err");
            e.printStackTrace();
        }

        // Our sample is in the root of this directory, so no package name.
        // CompilationUnit cu = sourceRoot.parse("", "Blabla.java");
        CompilationUnit cu = sourceRoot.parse("", "LambdaSource.java");
        Log.info("Positivizing!");


        cu.accept(new ModifierVisitor<Void>() {
            /**
             * For every if-statement, see if it has a comparison using "!=".
             * Change it to "==" and switch the "then" and "else" statements around.
             */
            /*
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                // Figure out what to get and what to cast simply by looking at the AST in a debugger!
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        /* It's a good idea to clone nodes that you move around.
                            JavaParser (or you) might get confused about who their parent is!
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    }
                });
                return super.visit(n, arg);
                        */
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
    }
}
