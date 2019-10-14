package com.pigman.community.service;

import com.pigman.community.domain.*;
import com.pigman.community.dto.CommentDTO;
import com.pigman.community.enums.CommentTypeEnum;
import com.pigman.community.enums.NotificationTypeEnum;
import com.pigman.community.enums.NotificationEnum;
import com.pigman.community.exception.CustomizeErrorCode;
import com.pigman.community.exception.CustomizeException;
import com.pigman.community.mapper.*;
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
    private CommentExtMapper commentExtMapper;

    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private QuestionExtMapper questionExtMapper;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User user) {
            if(comment.getParentId() == null || comment.getParentId() == 0){
                throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
            }
            if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
                throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
            }
            comment.setCommentCount((long) 0);
            if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
                //回复评论
                Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
                if(dbComment == null){
                    throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
                }
                Comment parentComment = new Comment();
                parentComment.setId(comment.getParentId());
                parentComment.setCommentCount((long) 1);



                Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
                if(question == null){
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }


                commentExtMapper.incComment(parentComment);
                commentMapper.insert(comment);

                createNotify(comment, dbComment.getCommentator(), user.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT,question.getId());





            }else{
                //回复问题
                Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
                if(question == null){
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incComment(question);
                createNotify(comment,question.getCreator(),user.getName(),question.getTitle(), NotificationTypeEnum.REPLY_QUESTION,question.getId());
            }
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {

        //获取指定问题的全部评论数据
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id).andTypeEqualTo(type.getType());
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

    /**
     * 记录回复消息到数据库
     * @param comment
     * @param receiver
     * @param name
     * @param title
     * @param notificationEnum
     * @param outerid
     */
    private void createNotify(Comment comment, Long receiver, String name, String title, NotificationTypeEnum notificationEnum,Long outerid){
        if(receiver == comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(comment.getCommentator());
        notification.setNotifierName(name);
        notification.setReceiver(receiver);
        notification.setType(notificationEnum.getType());
        notification.setOuterTitle(title);
        notification.setOuterid(outerid);//问题跳转id
        notification.setStatu(NotificationEnum.UNREAD.getState());
        notificationMapper.insert(notification);
    }
}
