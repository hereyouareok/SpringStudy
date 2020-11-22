package com.yan.qing.jd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description : 首页搜索
 * @Create on : 2020/11/22 20:21
 **/
@Controller
public class indexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
