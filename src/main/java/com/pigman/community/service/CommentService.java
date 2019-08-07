package com.pigman.community.service;

import com.pigman.community.domain.*;
import com.pigman.community.dto.CommentDTO;
import com.pigman.community.enums.CommentTypeEnum;
import com.pigman.community.exception.CustomizeErrorCode;
import com.pigman.community.exception.CustomizeException;
import com.pigman.community.mapper.CommentMapper;
import com.pigman.community.mapper.QuestionExtMapper;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private QuestionExtMapper questionExtMapper;


    @Autowired
    private UserMapper userMapper;


    @Transactional
    public void insert(Comment comment) {
            if(comment.getParentId() == null || comment.getParentId() == 0){
                throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
            }
            if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
                throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
            }
            if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
                //回复评论
                Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
                if(dbComment == null){
                    throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
                }
                commentMapper.insert(comment);
            }else{
                //回复问题
                Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
                if(question == null){
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incComment(question);
            }
    }

    public List<CommentDTO> listByQuestionId(Long id) {

        //获取指定问题的全部评论数据
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id).andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if(comments.size() == 0){
            return new ArrayList<>();
        }

        //获取全部的评论人，同时去除重复的部分，避免多次查询
        Set<Long> creators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(creators);

        //查找全部联系人的信息
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user->user.getId(),user->user));



        //将comment转换为commentDTO
        List<CommentDTO> commentDTOList = comments.stream().map(comment->{
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOList;
    }
}
