package com.pigman.community.controller;

import com.pigman.community.dto.CommentDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.enums.CommentTypeEnum;
import com.pigman.community.service.CommentService;
import com.pigman.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(
            @PathVariable(name="id")Long id,
            Model model){
        QuestionDTO questionDTO = questionService.findById(id);


        //查找问题的一级评论
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        //查找与tag相关的问题
        List<QuestionDTO> releatedQuestions = questionService.selectReleated(questionDTO);

        //累加评论
        questionService.incView(id);
        //System.out.println(questionDTO.toString());
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("releatedQuestions",releatedQuestions);
        return "question";
    }

}
