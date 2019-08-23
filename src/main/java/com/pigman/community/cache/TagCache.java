package com.pigman.community.cache;

import com.pigman.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {

    /**
     * 获得自定的全部标签
     * @return
     */
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("c","c#","java","python","R","html","javascript","node.js","php","lua","ruby","asp.net","shell"));
        tagDTOS.add(program);


        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring","express","django","flask","koa"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux","nginx","docker","apache","docker","centos","负载均衡"));
        tagDTOS.add(server);


        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql","sql server","mongoDB","redis"));
        tagDTOS.add(db);

        TagDTO other = new TagDTO();
        other.setCategoryName("其他");
        other.setTags(Arrays.asList("idea","git","maven"));
        tagDTOS.add(other);

        return tagDTOS;
    }


    /**
     * 验证输入标签是否合法
     * @param tags
     * @return
     */
    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags,',');
        List<TagDTO> tagDTOS = TagCache.get();

        List<String> tagList = tagDTOS.stream().flatMap(tag->tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(tag->!tagList.contains(tag)).collect(Collectors.joining(","));
        return invalid;
    }
}


