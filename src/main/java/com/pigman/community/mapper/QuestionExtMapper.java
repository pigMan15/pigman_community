package com.pigman.community.mapper;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.QuestionExample;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.dto.QuestionQueryDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int  incView(Question record);
    int incComment(Question question);
    List<Question> selectReleated(Question Question);

    Integer countBysearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}