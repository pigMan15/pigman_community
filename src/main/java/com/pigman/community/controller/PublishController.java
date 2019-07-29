package com.pigman.community.controller;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.User;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam( value="title",required=false) String title,
                            @RequestParam(value="description",required=false) String description,
                            @RequestParam(value="tag",required=false)String tag,
                            HttpServletRequest request,
                            Model model){

        //当标题、描述或者标签为空时，返回错误提示到前端页面
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }

        if(description == null || description == ""){
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }

        if(tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        //满足发布条件时，判断用户当前是否处于登录状态
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error","用户未登录");
            request.getSession().setAttribute("user",null);
            return "publish";
        }

        //将发布内容存储到数据库中
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.save(question);
        return "redirect:/";




    }

}
