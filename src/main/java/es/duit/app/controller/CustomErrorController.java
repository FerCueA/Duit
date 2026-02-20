package es.duit.app.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    // Para el 403 (viene desde Spring Security)
    @GetMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }

    // Para 404 y 500 (y fallback general)
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;

        // Si quieres, pásalo a la vista
        // model.addAttribute("statusCode", statusCode);

        if (statusCode == 404)
            return "error/404";
        if (statusCode == 403)
            return "error/403"; // por si cae aquí alguna vez
        return "error/500";
    }
}