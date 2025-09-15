package com.vic.textvalidator.Controller;


import com.vic.textvalidator.Service.UsernameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UsernameController {

    private final UsernameService usernameService;

    public UsernameController(UsernameService usernameService) {
        this.usernameService = usernameService;
    }

    @

    GetMapping("/check-username")
    public ResponseEntity<String> checkUsername(@RequestParam String username){
        boolean available = usernameService.checkUsername(username);

        if (available) {
            return ResponseEntity.ok(
                    "✅ " + username + " is available"
            ); } else{
                List<String> suggestions = usernameService.SuggestUsernames(username, 3);

                return ResponseEntity.ok(
                        "❌ " + username + " is taken, useful suggestions: " + String.join(", ", suggestions)
               );

        }
    }
}