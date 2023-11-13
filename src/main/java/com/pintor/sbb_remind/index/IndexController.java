package com.pintor.sbb_remind.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String root() {

        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {

        return "index";
    }

}
