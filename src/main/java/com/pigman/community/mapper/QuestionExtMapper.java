package com.pigman.community.mapper;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int  incView(Question record);
    int incComment(Question question);
}