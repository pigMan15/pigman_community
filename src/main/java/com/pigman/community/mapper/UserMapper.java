package com.pigman.community.mapper;

import com.pigman.community.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {


    @Insert("insert into `user` ( `ACCOUNT_ID`,`NAME`,`TOKEN`,`GMT_CREATE`,`GMT_MODIFIED`,`AVATAR_URL`) values(#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl});")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty = "Id")
    void save(User user);

    @Select("select * from user where TOKEN = #{token}")
    User findByToken(@Param("token") String token);


    @Select("select * from `user` where `id` =  #{id}")
    User findById(@Param("id")Integer id);

}

