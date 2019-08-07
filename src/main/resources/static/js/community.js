function post(){
    var questionId = $("#question_id").val();
    var commentContent = $("#comment_content").val();
    if(!commentContent){
        alert("内容不能为空！");
    }
    $.ajax({
       type:"POST",
       url:"/comment",
       contentType:"application/json",
       data:JSON.stringify({
           "parentId":questionId,
           "content":commentContent,
           "type":1
       }),
       success:function(response){
           console.log(response)
           if(response.code == 200){
               //评论提交成功时，刷新页面
                window.location.reload();
           }else{
               if(response.code == 2003){
                   var isLogin = confirm("未登录，是否登录？");
                   if(isLogin){
                       window.open("https://github.com/login/oauth/authorize?client_id=3605232d8c7cceafdbe2&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                       window.localStorage.setItem("closable",true);
                   }
               }else{
                   alert(response.message)
               }
           }
       },
       dataType:"json"
    });
    console.log(questionId);
    console.log(commentContent);
}