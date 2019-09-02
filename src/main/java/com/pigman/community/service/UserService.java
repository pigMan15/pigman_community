package com.pigman.community.service;

import com.pigman.community.domain.User;
import com.pigman.community.domain.UserExample;
import com.pigman.community.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
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


    public void updateActive(String action,String accountId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(accountId);
        List<User>users = userMapper.selectByExample(userExample);
        if(users.size() == 0){
            return ;
        }else{
            User updateUser = new User();
            updateUser.setAccountId(users.get(0).getAccountId());
            if(StringUtils.equals(action,"active")){
                updateUser.setActive((long) 1);
            }else if(StringUtils.equals(action,"negative")){
                updateUser.setActive((long) 0);
            }
            UserExample userExample1  = new UserExample();
            userExample.createCriteria().andAccountIdEqualTo(users.get(0).getAccountId());
            userMapper.updateByExampleSelective(updateUser,userExample);
        }
    }

    public List<User> findUsersByActive() {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andActiveEqualTo((long) 1);
        return userMapper.selectByExample(userExample);
    }

    public List<User> findUsersByNegative() {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andActiveEqualTo((long) 0);
        return userMapper.selectByExample(userExample);
    }

    public Object findUserById(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        return userMapper.selectByExample(userExample);
    }
}
