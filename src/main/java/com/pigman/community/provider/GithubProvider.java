package com.pigman.community.provider;


import com.alibaba.fastjson.JSON;
import com.pigman.community.dto.AccessTokenDTO;
import com.pigman.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

@Component
public class GithubProvider {


    public GithubUser getAccessToken(AccessTokenDTO accessTokenDTO){

       MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

          RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String tokenStr = response.body().string();
                //System.out.println(tokenStr);
                String token = tokenStr.split("&")[0].split("=")[1];
                return getGithubUser(token);

            }catch (Exception e){

            }

        return null;
    }



    public GithubUser getGithubUser(String accessToken){

        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token="+accessToken)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String result = response.body().string();
                GithubUser githubUser = JSON.parseObject(result,GithubUser.class);
                //System.out.println(githubUser.getName());
                return githubUser;
            }catch (Exception e){

            }
            return null;
    }


}
