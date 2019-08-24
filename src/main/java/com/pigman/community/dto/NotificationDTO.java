package com.pigman.community.dto;

import com.pigman.community.domain.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer statu;
    private Long notifier;
    private String notifierName;
    private String  outerTitle;
    private Long outerid;
    private String typeName;
    private Integer type;
}
