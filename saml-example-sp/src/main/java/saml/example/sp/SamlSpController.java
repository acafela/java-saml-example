package saml.example.sp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SamlSpController {

    @GetMapping("/user")
    String user() {
        return "name : yshwang";
    }

    @GetMapping("/test")
    String test() {
        return "name : test";
    }

    @PostMapping("/acs")
    String acs() {
        return "name : test";
    }

}
