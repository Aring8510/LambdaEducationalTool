package app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("msg", "Hello World!!");
        return "index";
    }
    @GetMapping(value = "/sourceInput")
    public String sourceInputForm (Model model) {
        model.addAttribute("sourceInput", new SourceInput());
        return "sourceInput";
    }
    @PostMapping(value = "/sourceInput")
    public String sourceInputSubmit (@ModelAttribute SourceInput si, @ModelAttribute SourceRecordStorage sourceRecordStorage, Model model) {
        sourceRecordStorage = Main.parse(si.getSource());
        sourceRecordStorage.parseMethodRecord();
        sourceRecordStorage.parseLambdaRecord();

        si.createHighlightedSource(sourceRecordStorage);
        System.out.println(sourceRecordStorage.getColorCounter());

        model.addAttribute("sourceInput", si);
        model.addAttribute("srs", sourceRecordStorage);

        System.out.println("finished");
        return "result";
    }
}



