package com.pigman.community.controller;

import com.pigman.community.domain.User;
import com.pigman.community.dto.PaginationDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.mapper.UserMapper;
import com.pigman.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/{action}")
    public String index(HttpServletRequest request,
                        Model model,
                        @PathVariable(name="action",required = false) String action,
                        @RequestParam(name="page",defaultValue = "1") Integer page,
                        @RequestParam(name="size",defaultValue = "5") Integer size,
                        @RequestParam(name="search",required = false) String search){


        String requestURL = "";
        if(StringUtils.isBlank(requestURL)){
            requestURL= "http://"+request.getServerName()+":"+request.getServerPort();
            request.getSession().setAttribute("loginUrl",requestURL);
        }
        //首页跳转，清除搜索条件
        if(StringUtils.equals(action,"index")){
            request.getSession().setAttribute("search", "");
            action="latest";
        }

        /**
         * 存储搜索条件，使得最新，最热，消灭0回复功能可以在search的前提下进一步分类
         */
        if(search != null) {
            request.getSession().setAttribute("search", search);
        }else{
            search = (String) request.getSession().getAttribute("search");
        }

        System.out.println(action);
        PaginationDTO paginationDTO = null;



        if(StringUtils.equals(action,"latest") || StringUtils.equals(action,"error")){
                action ="latest";
                paginationDTO = questionService.list(action,search,page,size);
        }else if(StringUtils.equals(action,"hot")){
             paginationDTO = questionService.list(action,search,page,size);
        }else if(StringUtils.equals(action,"zero")) {
            paginationDTO = questionService.list(action,search,page,size);
        }

        model.addAttribute("pagination",paginationDTO);
        model.addAttribute("search",search);
        model.addAttribute("path",action);
        return "index";
    }



}
