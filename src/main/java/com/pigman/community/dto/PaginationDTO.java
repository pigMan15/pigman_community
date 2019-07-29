package com.pigman.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();;
    private Integer totalPage;

    public void setPagination(Integer page, Integer totalPage) {

        //存储选中的页数,总页数到对象中
        this.totalPage = totalPage;
        this.page = page;



        //添加当前页及当前页的前，后3个页码
        pages.add(page);
        for(int i = 1; i<=3; i++){
            if(page - i > 0){
                pages.add(0,page-i);
            }

            if(page + i <= totalPage){
                pages.add(page + i);
            }
        }



        //向前，向后等判断逻辑
        if(page == 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }


        if(page == totalPage){
            showNext = false;
        }else{
            showNext = true;
        }

        if(pages.contains(1)) {
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }

        if(pages.contains(totalPage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
