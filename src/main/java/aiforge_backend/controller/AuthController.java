package aiforge_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import aiforge_backend.model.User;
import aiforge_backend.repository.UserRepository;

@RestController
@CrossOrigin("*")

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")

    public String register(
            @RequestBody User user
    ) {

        userRepository.save(user);

        return "User Registered Successfully";
    }

    @PostMapping("/login")

    public Object login(
            @RequestBody User user
    ) {

        User existingUser =
                userRepository.findByEmail(
                        user.getEmail()
                );

        if (

                existingUser != null &&

                existingUser.getPassword()
                        .equals(user.getPassword())

        ) {

            return Map.of(

                    "message",
                    "Login Success",

                    "user",
                    existingUser
            );
        }

        return Map.of(

                "message",
                "Invalid Email or Password"
        );
    }
}