package com.pigman.community.controller;


import com.pigman.community.domain.Comment;
import com.pigman.community.domain.User;
import com.pigman.community.dto.CommentCreateDTO;
import com.pigman.community.dto.CommentDTO;
import com.pigman.community.dto.ResultDTO;
import com.pigman.community.enums.CommentTypeEnum;
import com.pigman.community.exception.CustomizeErrorCode;
import com.pigman.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private CommentService commentService;


    /**
     *
     * 评论功能（一，二级）
     * @param commentCreateDTO
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentCreateDTO == null|| StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());
        comment.setContent(commentCreateDTO.getContent());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        commentService.insert(comment);
        return ResultDTO.success();
    }


    /**
     * 查找二级评论
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/comment/{id}",method = RequestMethod.GET)
    public ResultDTO get(@PathVariable(name="id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.success(commentDTOS);
    }

}
