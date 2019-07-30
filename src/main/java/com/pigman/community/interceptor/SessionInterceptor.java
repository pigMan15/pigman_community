package com.pigman.community.interceptor;

import com.pigman.community.domain.User;
import com.pigman.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[ ] cookies =  request.getCookies();
        if(cookies != null && cookies.length != 0)
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("token")){
                    User user =  userMapper.findByToken(cookie.getValue());

                    //  System.out.println(request.getSession().getAttribute("user"));
                    if(user != null){
                        //System.out.println(user.toString());
                        request.getSession().setAttribute("user",user);
                    }else{
                        //当用户数据从数据库中被删除时，设置session中user的值为null，回到登录状态
                        request.getSession().setAttribute("user",null);
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}