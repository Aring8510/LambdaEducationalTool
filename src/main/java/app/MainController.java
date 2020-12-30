package app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String sourceInputSubmit (@ModelAttribute SourceInput si,  Model model) {
        System.out.println("submit");
        model.addAttribute("sourceInput", si);
        return "result";
    }
}
