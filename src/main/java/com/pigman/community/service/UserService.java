package com.pigman.community.service;

import com.pigman.community.domain.User;
import com.pigman.community.domain.UserExample;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
           UserExample userExample = new UserExample();
           userExample.createCriteria()
                   .andAccountIdEqualTo(user.getAccountId());
           List<User> users = userMapper.selectByExample(userExample);
           if(users.size() == 0){
               user.setGmtCreate(System.currentTimeMillis());
               user.setGmtModified(user.getGmtCreate());
               userMapper.insert(user);
           }else{
               User dbUser = users.get(0);
               User updateUser = new User();

               updateUser.setName(user.getName());
               updateUser.setToken(user.getToken());
               updateUser.setAvatarUrl(user.getAvatarUrl());
               updateUser.setGmtModified(System.currentTimeMillis());
               UserExample example = new UserExample();
               example.createCriteria()
                       .andIdEqualTo(dbUser.getId());
               userMapper.updateByExampleSelective(updateUser,example);
           }
    }
}
