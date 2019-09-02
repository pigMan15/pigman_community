package com.pigman.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @GetMapping("/user")
    @ResponseBody
    public Object findUserById(@RequestParam(name="id")Integer id){
        return null;
    }

}
