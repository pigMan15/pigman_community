package com.pigman.community.service;

import com.pigman.community.domain.Question;
import com.pigman.community.domain.QuestionExample;
import com.pigman.community.domain.User;
import com.pigman.community.dto.PaginationDTO;
import com.pigman.community.dto.QuestionDTO;
import com.pigman.community.dto.QuestionQueryDTO;
import com.pigman.community.exception.CustomizeErrorCode;
import com.pigman.community.exception.CustomizeException;
import com.pigman.community.mapper.QuestionExtMapper;
import com.pigman.community.mapper.QuestionMapper;
import com.pigman.community.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private QuestionExtMapper questionExtMapper;




    public PaginationDTO list(String key,String search,Integer page, Integer size){


        if(StringUtils.isNotBlank(search)){
            String[] splits = StringUtils.split(search," ");
            search = Arrays.stream(splits).collect(Collectors.joining("|"));
        }
        PaginationDTO paginationDTO = new PaginationDTO();

        //对page < 0 或page > totalPage 非法请求进行处理
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setMykey(key);
        Integer totalCount = questionExtMapper.countBysearch(questionQueryDTO);
        if(totalCount == 0){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
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








        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);

        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

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
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(page,totalPage);

        return paginationDTO;
    }


    public PaginationDTO list(Long userId,Integer page, Integer size){
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
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(page,totalPage);

        return paginationDTO;
    }

    public QuestionDTO findById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
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
                question.setViewCount(0);
                question.setCommentCount(0);
                question.setLikeCount(0);
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
                Integer result = questionMapper.updateByExampleSelective(updateQuestion,example);
                if(result != 1){
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }
            }
    }


    /**
     * 累加评论
     * @param id
     */
    public void incView(Long id) {
//        Question question = questionMapper.selectByPrimaryKey(id);
//        Question updateQuestion = new Question();
//        updateQuestion.setViewCount(question.getViewCount() + 1);
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.createCriteria()
//                .andIdEqualTo(id);
//        questionMapper.updateByExampleSelective(updateQuestion,questionExample);
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }


    /**
     * 根据tag查询相关问题
     * @param queryDTO
     * @return
     */
    public  List<QuestionDTO> selectReleated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }

        String[] tags = StringUtils.split(queryDTO.getTag(),",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectReleated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q->{

            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;
    }
}
