package com.pigman.community.service;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.User;
import com.pigman.community.dto.PaginationDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;


    public PaginationDTO list(Integer page, Integer size){

        //对page < 0 或page > totalPage 非法请求进行处理
        Integer totalCount = questionMapper.count();
        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        //偏移量
        Integer offset  = size*(page-1);

        PaginationDTO paginationDTO = new PaginationDTO();
        totalCount = questionMapper.count();

        List<Question> questions = questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        User user = new User();



        for(Question question : questions){
            user=userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }

        //装配pagination
        paginationDTO.setQuestions(questionDTOList);
        paginationDTO.setPagination(page,totalPage);

        return paginationDTO;
    }


    public PaginationDTO list(Integer userId,Integer page, Integer size){
        //对page < 0 或page > totalPage 非法请求进行处理
        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        //偏移量
        Integer offset  = size*(page-1);

        PaginationDTO paginationDTO = new PaginationDTO();
        totalCount = questionMapper.countByUserId(userId);

        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        User user = new User();








        for(Question question : questions){
            user=userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setUser(user);
            BeanUtils.copyProperties(question,questionDTO);
            questionDTOList.add(questionDTO);
        }

        //装配pagination
        paginationDTO.setQuestions(questionDTOList);
        paginationDTO.setPagination(page,totalPage);

        return paginationDTO;
    }

}
