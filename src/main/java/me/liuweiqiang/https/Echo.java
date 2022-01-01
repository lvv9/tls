package me.liuweiqiang.https;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Echo {

    @GetMapping("/echo")
    public String echo() {
        return "Hello world";
    }
}
