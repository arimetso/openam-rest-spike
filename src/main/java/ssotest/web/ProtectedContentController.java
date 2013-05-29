package ssotest.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProtectedContentController {
    @RequestMapping(value="/content/page/{pagenum}", method=RequestMethod.GET)
    public String contentPage(Model model, @PathVariable("pagenum") int pagenum, HttpServletRequest request) {
        HomeController.logRequestAndSessionInfo(request);
        model.addAttribute("pagenum", pagenum);
        return "contentpage";
    }
}
