package org.marvel.deevy.extension.controller;

import cn.hutool.core.date.DatePattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoyuanqiang
 * @date 2022/10/16 16:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@RestController
@Slf4j
@RequestMapping("/business-code")
public class BusinessCodeController {
    

    @GetMapping("/next")
    public void generateBusinessCode() {
        String rule = "QT-genDate(\"yyyyMMdd\")-genSN(\"ABC\", 8)";
    }
}
