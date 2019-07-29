package com.pigman.community.controller;


import com.alibaba.fastjson.JSON;
import com.pigman.community.domain.User;
import com.pigman.community.dto.AccessTokenDTO;
import com.pigman.community.dto.GithubUser;
import com.pigman.community.mapper.UserMapper;
import com.pigman.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client_id}")
    private String githubClientId;

    @Value("${github.client_secret}")
    private String githubClientSecret;

    @Value("${github.redirect_url}")
    private String githubClientUrl;



    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code, @RequestParam(name="state") String state, HttpServletRequest request, HttpServletResponse response){


        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(githubClientId);
        accessTokenDTO.setClient_secret(githubClientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(githubClientUrl);
        GithubUser githubUser = githubProvider.getAccessToken(accessTokenDTO);
//      System.out.println(JSON.toJSONString(githubUser));
//      System.out.println("token="+token);
        if(githubUser != null){
            //登陆成功
//            request.getSession().setAttribute("user",githubUser);
            //保存当前登录用户信息到数据库
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatar_url());

            userMapper.save(user);

            response.addCookie(new Cookie("token",token));

            return "redirect:/";
        }else{
            //登录失败
            return "redirect:/";
        }
    }



}
