package saml.example.sp;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import static java.util.Objects.isNull;

@Controller
public class UserController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (isNull(authentication)) {
            return "index";
        }
        return authentication.isAuthenticated() ? "redirect:/user" : "index";
    }

    @GetMapping("user")
    public String user(Authentication authentication, ModelMap modelMap) {
        modelMap.addAttribute("user", authentication.getPrincipal());
        return "user";
    }

}