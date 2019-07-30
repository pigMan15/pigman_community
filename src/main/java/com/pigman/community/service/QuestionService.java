package com.pigman.community.service;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.QuestionExample;
import com.pigman.community.domain.User;
import com.pigman.community.dto.PaginationDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import org.apache.ibatis.session.RowBounds;
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
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
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


        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        User user = new User();



        for(Question question : questions){
            user=userMapper.selectByPrimaryKey(question.getCreator());
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
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

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));
       List<QuestionDTO> questionDTOList = new ArrayList<>();
        User user = new User();








        for(Question question : questions){
            user=userMapper.selectByPrimaryKey(question.getCreator());
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

    public QuestionDTO findById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }


    /**
     * 问题发布或更新功能
     * @param question
     */
    public void createOrUpdate(Question question) {
        //id不存在则新建数据库记录，存在则进行更新
            if(question.getId() == null){
                question.setGmtCreate(System.currentTimeMillis());
                question.setGmtModified(question.getGmtCreate());
                questionMapper.insert(question);
            }else{
                Question updateQuestion = new Question();
                updateQuestion.setGmtModified(System.currentTimeMillis());
                updateQuestion.setTitle(question.getTitle());
                updateQuestion.setDescription(question.getDescription());
                updateQuestion.setTag(question.getTag());
                QuestionExample example = new QuestionExample();
                example.createCriteria()
                        .andIdEqualTo(question.getId());
                questionMapper.updateByExampleSelective(updateQuestion,example);
            }
    }
}
