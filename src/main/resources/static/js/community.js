/**
 * 一级评论
 */
function post(){
    var questionId = $("#question_id").val();
    var commentContent = $("#comment_content").val();
    commment2target(questionId,commentContent,1);
}

/**
 * 抽取评论通用接口
 * @param targetId
 * @param content
 * @param type
 */
function commment2target(targetId,content,type){
    if(!content){
        alert("内容不能为空！");
    }
    $.ajax({
        type:"POST",
        url:"/comment",
        contentType:"application/json",
        data:JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
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
}

/**
 * 二级评论
 * @param e
 */
function comment(e){
    var targetId = e.getAttribute("data-id");
    var content = $('#reply-'+targetId).val();
    console.log(targetId);
    console.log(content);
    commment2target(targetId,content,2);
}


/**
 * 二级评论展开折叠控制
 * @param e
 */
function onCollapse (e){
    var id = e.getAttribute("data-id");
    var comments = $("#comment-"+id);


    var collapse = e.getAttribute("data-collapse");
    //折叠评论
    if(collapse){
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    }else{
    //展开评论
        $.getJSON("/comment/"+id,function(data){
            console.log(data.data);
            var subCommentContainer = $("#comment-"+id);

            if(subCommentContainer.children().length != 2){
                comments.addClass("in");
                e.setAttribute("data-collapse","in");
                e.classList.add("active");
            }else{
                $.each(data.data.reverse(),function(index,comment){


                    //编写二级评论页面组成元素
                    var mediaLeftElement = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>",{
                        "class":"media-object img-rounded",
                        "src":comment.user.avatarUrl
                    }));


                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h4/>",{
                        "class":"media-heading",
                        "html":comment.user.name
                    })).append($("<div/>",{
                        "html":comment.content
                    })).append($("<div/>",{
                        "class":"menu"
                    }).append($("<span/>",{
                        "class":"pull-right icon",
                        "html":moment(comment.gmtCreate).format("YYYY-MM-DD")
                    })));


                    var mediaElement = $("<div/>",{
                        "class":"media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);

                    var commentElement = $("<div/>",{
                        "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);


                    subCommentContainer.prepend(commentElement);

                });

                comments.addClass("in");
                e.setAttribute("data-collapse","in");
                e.classList.add("active");
            }



        });

    }

}


/**
 * 选择标签功能
 * @param e
 */
function selectTag(e){
    var value = e.getAttribute('data-tag');
    var previous = $("#tag").val();
    if(previous.indexOf(value) == -1){
        if(previous){
            $("#tag").val(previous+','+value);
        }else{
            $("#tag").val(value);
        }
    }
}

function showSelectTag(){

    $("#select-tag").show();
}

function displaySelectTag(){
    console.log('1')
    $("#select-tag").style.display="none"
}