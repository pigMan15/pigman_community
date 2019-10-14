package com.pigman.community.Schedule;

import com.pigman.community.cache.HotTagCache;
import com.pigman.community.domain.Question;
import com.pigman.community.domain.QuestionExample;
import com.pigman.community.mapper.QuestionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Component
@Slf4j

public class HotTagTasks {


        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        @Autowired
        private QuestionMapper questionMapper;

        @Autowired
        private HotTagCache hotTagCache;

        @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
        public void reportCurrentTime() {
            int offset = 0;
            int limit = 5;
            log.info("HotTagTasks start{} ",new Date());
            List<Question> lists = new ArrayList<>();
            Map<String,Integer> priorities = new HashMap<>();
            while(offset == 0 || lists.size() == limit){
                lists = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,limit));
                for (Question question : lists) {
                    log.info("list question id is {}",question.getId());

                    String[] tags = StringUtils.split(question.getTag(),",");
                    for (String tag : tags) {
                        Integer priority = priorities.get(tag);
                        if(priority != null ){
                            priorities.put(tag,priority + 5 + question.getCommentCount() + question.getViewCount());
                        }else{
                            priorities.put(tag,5 + question.getCommentCount() + question.getViewCount());
                        }
                    }
                }
                offset+=limit;
            }


//            priorities.forEach(
//                    (k,v)->{
//                        System.out.print(k);
//                        System.out.print(":");
//                        System.out.print(v);
//                        System.out.println();
//                    });

            hotTagCache.updateTags(priorities);
            log .info("HotTagTask stop {}",new Date());
        }


}
