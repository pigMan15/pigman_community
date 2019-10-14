package com.pigman.community.controller;

import com.pigman.community.cache.TagCache;
import com.pigman.community.domain.Question;
import com.pigman.community.domain.User;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import com.pigman.community.service.QuestionService;
import com.pigman.community.service.SensitiveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {



    @Autowired
    private QuestionService questionService;

    @Autowired
    private SensitiveService sensitiveService;


    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }


    @PostMapping("/publish")
    public String doPublish(@RequestParam( value="title",required=false) String title,
                            @RequestParam(value="description",required=false) String description,
                            @RequestParam(value="tag",required=false)String tag,
                            @RequestParam(value="id",required=false) Long id,
                            HttpServletRequest request,
                            Model model){

        //当标题、描述或者标签为空时，返回错误提示到前端页面
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
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

        String invalid = TagCache.filterInvalid(tag);
        if(!StringUtils.isBlank(invalid)){
            model.addAttribute("error","输入非法标签："+invalid);
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
        question.setTitle(sensitiveService.filter(title));
        question.setDescription(sensitiveService.filter(description));
        question.setTag(sensitiveService.filter(tag));
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setId(id);

        questionService.createOrUpdate(question);
        return "redirect:/";




    }

    /**
     * 编辑功能跳转
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name="id")Long id,
                       Model model){
        QuestionDTO question = questionService.findById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

}
