package com.pigman.community.controller;

import com.pigman.community.domain.User;
import com.pigman.community.dto.PaginationDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.mapper.UserMapper;
import com.pigman.community.service.NotificationService;
import com.pigman.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name="action")String action,
                          @RequestParam(name="page",defaultValue = "1") Integer page,
                          @RequestParam(name="size",defaultValue = "5") Integer size,
                          Model model,
                          HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        if(user == null){
            return "redirect:/";
        }




        //System.out.println(action);
            if("questions".equals(action)){
                model.addAttribute("section","questions");
                model.addAttribute("sectionName","我的问题");
                PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
                model.addAttribute("pagination",paginationDTO);
            } else if ("replies".equals(action)) {
                Long unReadCount = notificationService.unReadCount(user.getId());
                model.addAttribute("section","replies");
                model.addAttribute("sectionName","最新回复");
                PaginationDTO paginationDTO = notificationService.list(user.getId(),page,size);
                model.addAttribute("pagination",paginationDTO);
                model.addAttribute("unReadCount",unReadCount);
            }
            return "profile";
    }



}
