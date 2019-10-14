package com.pigman.community.cache;


import com.pigman.community.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class HotTagCache {


    private List<String> hots = new ArrayList<>();

    public void updateTags(Map<String,Integer> tags){
        int max = 5;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);

        tags.forEach((name,priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);

            if(priorityQueue.size() < 5){
                priorityQueue.add(hotTagDTO);
            }else{
                HotTagDTO min = priorityQueue.peek();
                if(hotTagDTO.getPriority() > min.getPriority()) {
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });

        List<String> sortedTags = new ArrayList<>();

        HotTagDTO hotTagDTO = priorityQueue.poll();
        while(hotTagDTO != null){
            sortedTags.add(0,hotTagDTO.getName());
            hotTagDTO = priorityQueue.poll();
        }

        hots = sortedTags;

    }
}
