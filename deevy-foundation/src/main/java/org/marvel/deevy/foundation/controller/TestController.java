package org.marvel.deevy.foundation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoyuanqiang
 * @date 2022/9/6 17:33
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@RestController
public class TestController {

    @GetMapping("/public/test")
    public String Hello() {
        return "Hello, Deevy Foundation";
    }


    @GetMapping("/protected/test")
    public String test() {
        return "This is protected method";
    }


    @GetMapping("/private/test")
    public String test2() {
        return "This is private method";
    }
}
