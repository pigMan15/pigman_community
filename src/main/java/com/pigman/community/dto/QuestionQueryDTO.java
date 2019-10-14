package com.pigman.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {

    private String search;
    private String tag;
    private String type;
    private Integer page;
    private Integer size;

}
