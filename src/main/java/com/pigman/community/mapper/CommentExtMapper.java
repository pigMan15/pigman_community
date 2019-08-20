package com.pigman.community.mapper;

import com.pigman.community.domain.Comment;

public interface CommentExtMapper {

    /**
     * 评论数的增加方法
     * @param comment
     * @return
     */
    int incComment(Comment comment);
}