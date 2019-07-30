package com.pigman.community.service;

import com.pigman.community.domain.User;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
           User dbUser = userMapper.findByAccountId(user.getAccountId());
           if(dbUser == null){
               user.setGmtCreate(System.currentTimeMillis());
               user.setGmtModified(user.getGmtCreate());
               userMapper.save(user);
           }else{
               dbUser.setName(user.getName());
               dbUser.setToken(user.getToken());
               dbUser.setAvatarUrl(user.getAvatarUrl());
               dbUser.setGmtModified(System.currentTimeMillis());
               userMapper.update(dbUser);
           }
    }
}
