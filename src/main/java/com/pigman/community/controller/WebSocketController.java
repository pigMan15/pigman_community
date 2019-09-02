package com.pigman.community.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebSocketController {

        @GetMapping("/websocket")
        public  String websocket(){
            return "WebSocketTest";
        }

}
