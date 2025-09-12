package com.vic.textvalidator.Controller;


import com.vic.textvalidator.Service.UsernameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsernameController {

    private final UsernameService usernameService;

    public UsernameController(UsernameService usernameService) {
        this.usernameService = usernameService;
    }

    @GetMapping("/check-username")
    public String checkUsername(@RequestParam String username) {
        boolean available = usernameService.checkUsername(username);
        return available? "✅ Username is available" : "❌ Username is taken";
    }
}
