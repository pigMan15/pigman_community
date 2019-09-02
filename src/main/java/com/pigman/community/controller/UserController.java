package com.pigman.community.controller;

import com.pigman.community.domain.User;
import com.pigman.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/{action}",method = RequestMethod.POST)
    @ResponseBody
    public Object updateActive(@PathVariable(name="action",required=true)String action,@RequestBody User user){

        if(StringUtils.equals(action,"active") || StringUtils.equals(action,"negative")){
            userService.updateActive(action,user.getAccountId());
        }
        return null;
    }


    @RequestMapping(value = "/findUsersByActive")
    @ResponseBody
    public Object findUsersByActive(){
        List<User> users = userService.findUsersByActive();
        return users;
    }

    @RequestMapping(value = "/findUsersByNegative")
    @ResponseBody
    public Object findUsersByNegative(){
        List<User> users = userService.findUsersByNegative();
        return users;
    }


    @RequestMapping(value="/findUserById",method=RequestMethod.POST)
    @ResponseBody
    public Object findUserById(@RequestBody User user){
        return userService.findUserById(user);
    }
}
