package aiforge_backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")

public class AuthController {

    @GetMapping("/test")
    public String test() {
        return "Backend Working";
    }
}