<#include "macro-head.ftl">
<#include "common/sub-nav.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${symphonyLabel}">
        <meta name="description" content="${symDescriptionLabel}"/>
        </@head>
    </head>
    <body class="index">
        ${HeaderBannerLabel}
        <#include "header.ftl">
        <@subNav '' ''/>
        <div class="main">
       		<#-- <button onclick="jump()">跳转 </button> -->
            <div class="wrapper fn-clear">
            	
            	<#if recentArticles?size != 0>	
                <div class="item mid">
                    <a href="${servePath}/recent" class="item-header latestIcon">${latestLabel}</a>
                    <div class="module-panel">
                        <ul class="module-list" id="newList">
                            
                        </ul>
                    </div>
                </div>
                <a id="clickMore" flag="true">点击加载更多</a>
                </#if>
                
                <#if isLoggedIn && followingArticles?size != 0>
                <div class="item">
                    <a href="${servePath}/member/${currentUser.userName}/watching/articles" class="item-header articleIcon">${watchingArticlesLabel}</a>
                    <#--<div class="module-panel">
                        <ul class="module-list">
                            <#list followingArticles as article>
                            	<#include "common/list-item.ftl">
                            <li<#if !article_has_next> class="last"</#if>>
                                <#if "someone" != article.articleAuthorName>
                                <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}"></#if>
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                                    <#if "someone" != article.articleAuthorName></a></#if>
                                <a rel="nofollow" class="title" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}">${article.articleViewCount}</a>
                            </li>
                            </#list>
                        </ul>
                    </div>-->
                </div>
                </#if>

				<#if perfectArticles?size != 0>
                <div class="item">
                    <a href="${servePath}/perfect" class="item-header qualityIcon">${perfectLabel}</a>
                    <#--<div class="module-panel">
                        <ul class="module-list">
                            <#list perfectArticles as article>
                            	<#include "common/list-item.ftl">
                            <li<#if !article_has_next> class="last"</#if>>
                                <#if "someone" != article.articleAuthorName>
                                <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}"></#if>
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                                    <#if "someone" != article.articleAuthorName></a></#if>
                                <a rel="nofollow" class="title" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}">${article.articleViewCount}</a>
                            </li>
                            </#list>
                        </ul>
                    </div>-->
                </div>
                </#if>
                
                <#if isLoggedIn && followingTagArticles?size != 0>
                <div class="item">
                    <a href="${servePath}/member/${currentUser.userName}/following/tags" class="item-header biaoIcon">${followingTagsLabel}</a>
                    <#--<div class="module-panel">
                        <ul class="module-list">
                            <#list followingTagArticles as article>
                            	<#include "common/list-item.ftl">
                            <li<#if !article_has_next> class="last"</#if>>
                                <#if "someone" != article.articleAuthorName>
                                <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}"></#if>
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                                    <#if "someone" != article.articleAuthorName></a></#if>
                                <a rel="nofollow" class="title" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}">${article.articleViewCount}</a>
                            </li>
                            </#list>
                        </ul>
                    </div>-->
                </div>
                </#if>
            
            </div>
        </div>
        
     <#--   <#if tags?size != 0>
        <div class="index-wrap">
            <div class="wrapper">
                <ul class="tag-desc fn-clear">
                    <#list tags as tag>
                    <li>
                        <a rel="nofollow" href="${servePath}/tag/${tag.tagURI}">
                            <#if tag.tagIconPath!="">
                            <img src="${staticServePath}/images/tags/${tag.tagIconPath}" alt="${tag.tagTitle}" />
                            </#if>
                            ${tag.tagTitle}
                        </a>
                    </li>
                    </#list>
                </ul>
            </div>
        </div>
        </#if> -->
        
     <#--   <div class="main">
            <div class="wrapper">
                <div class="item">
                    <a href="${servePath}/timeline" class="item-header" style="background-image: url(${timelineBgIcon});">${timelineLabel}</a>
                    <div class="module-panel">
                        <#if timelines?size <= 0>
                        <div id="emptyTimeline">${emptyTimelineLabel}</div>
                        </#if>
                        <ul class="module-list">
                            <#list timelines as article>
                            <#if article_index < 3>
                            <li<#if !article_has_next> class="last"</#if>>
                                ${article.content}
                                </#if>
                        </li>
                        </#list>
                    </ul>
                </div>
            </div>
            <#if ADLabel != ''>
            <div class="item mid">
                <a class="item-header" style="background-image: url(${adBgIcon})" href="https://hacpai.com/article/1460083956075">${sponsorLabel}</a>
                <div class="ad module-panel">
                    ${ADLabel}
                </div>
            </div>
            </#if>
            <div class="item">
                <a class="item-header" style="background-image: url(${activityBgIcon});" href="${servePath}/pre-post">${postArticleLabel}</a>
                <div class="module-panel">
                    <ul class="module-list">
                        <li><a class="title" href="<#if useCaptchaCheckin??>${servePath}/activity/checkin<#else>${servePath}/activity/daily-checkin</#if>">${activityDailyCheckinLabel}</a></li>
                        <li><a class="title" href="${servePath}/activity/yesterday-liveness-reward">${activityYesterdayLivenessRewardLabel}</a></li>
                        <li><a class="title" href="${servePath}/activity/1A0001">${activity1A0001Label}</a></li>
                        <li><a class="title" href="${servePath}/activity/character">${characterLabel}</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div> -->


    <#--<div class="slogan">
        ${indexIntroLabel}&nbsp;
        <a href="https://github.com/b3log/symphony" target="_blank">
            <svg class="ft-gray" height="16" width="16" viewBox="0 0 16 16">${githubIcon}</svg></a>
        <a href="http://weibo.com/u/2778228501" target="_blank">
            <svg class="ft-gray" width="18" height="18" viewBox="0 0 37 30">${weiboIcon}</svg></a>    
        <a target="_blank"
           href="http://shang.qq.com/wpa/qunwpa?idkey=981d9282616274abb1752336e21b8036828f715a1c4d0628adcf208f2fd54f3a">
            <svg class="ft-gray" width="16" height="16" viewBox="0 0 30 30">${qqIcon}</svg></a>
    </div>-->
    <#include "footer.ftl">
</body>
</html>
<script>
	<#if isLoggedIn>
        Label.currentUserName = '${currentUser.userName}';
        Label.currentUserAvatarURL = '${currentUser.userAvatarURL48}';
        Label.currentUserNickname = '${currentUser.userNickname}';
        Label.currentUserOId = '${currentUser.oId}';
    </#if>


    var page=0;
    $("#clickMore").click(function(){
        if($(this).attr('flag')=='true'){
            takeArticles(page,1);
        }
    });
    $(window).scroll(function(){  
        sessionStorage.indexTop=$(window).scrollTop();
    });  
    function dateToStr(dateObj,DorM){//转换日期为字符串
        if(!dateObj){
            return "/";
        }
        dateObj=new Date(dateObj);
        var year=dateObj.getFullYear();
        var month=dateObj.getMonth()+1;
        var day=dateObj.getDate();
        var hour=dateObj.getHours();
        var minute=dateObj.getMinutes();
        var second=dateObj.getSeconds();
        year=year.toString();
        var trans=function(v){
            return (v<10?'0':'')+v;
        }
        month = trans(month);
        day = trans(day);
        hour = trans(hour);
        minute = trans(minute);
        second = trans(second);
        if(DorM=="month"){
            return year+"-"+month;
        }else if(DorM=="day"){
            return year+"-"+month+"-"+day;
        }else if(DorM=="hour"){
            return year+"-"+month+"-"+day+" "+hour;
        }else if(DorM=="minute"){
            return year+"-"+month+"-"+day+" "+hour+":"+minute;
        }else if(DorM=="second"){
            return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
        }else if(DorM=="onlyTime"){
            return hour+":"+minute+":"+second;
        }
    };
    function takeArticles(p,x) {
        $('#clickMore').html('正在加载').attr('flag','false');
        if(sessionStorage.indexPage && x==0){
            var url = Label.servePath + "/recent/more?p="+sessionStorage.indexPage+"&pageCount=yes";
        }else{
            var url = Label.servePath + "/recent/more?p="+(p+1)+"&pageCount=only";
            page=page+1;
        }
    	$.ajax({
            url: url,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                var list=result.latestArticles
                if(list.length<1){
                    $('#clickMore').html('已加载全部帖子').attr('flag','false');
                }else{
                    var $ul=$('#newList');
                    for(var i in list){
                    
                    	var anonymousType = 0;
                    	if(list[i].articleAnonymous == 1 ){
                    		if(Label.isLoggedIn && list[i].articleAuthorId == Label.currentUserOId){
                    			anonymousType = 1;
	                    	}else{
	                    		anonymousType = 2;
	                    	}
                    	}
                    	
                        var $li=$('<li></li>');
                        $li.append('<div>'+
                                        '<div>'+
                                            (anonymousType == 2 ? '': '<a rel="nofollow" href="'+Label.servePath+'/member/'+
                                            	(anonymousType == 1 ? Label.currentUserName : list[i].articleAuthorName) + '">')+
                                                '<div class="avatar" style="background-image:url('+(anonymousType == 1 ? Label.currentUserAvatarURL : list[i].articleAuthorThumbnailURL20)+')"></div>'+
                                            	(anonymousType == 2 ? '' : '</a>')+
                                            
                                            '<div class="fn-ellipsis ft-fade ft-smaller list-info userNameArea">'+
                                            
                                            (anonymousType == 2 ? '匿名用户' : ('<a rel="nofollow" class="author" href="'+Label.servePath+'/member/'+
                                            	(anonymousType == 1 ? Label.currentUserName : list[i].articleAuthorName)+'">'+
                                            	list[i].articleAuthor.userNickname+ 
                                            	(anonymousType == 1 ? '(已匿名)' :'')+
                                            	'</a><a class="'+list[i].articleAuthor.userLevelType+'">'+list[i].articleAuthor.userLevel+'</a>'))+
                                                
                                            '<br>'+
                                            dateToStr(list[i].articleCreateTime,'day')+
                                            '</div>'+

                                            '<h3 class="articleH3">'+
                                                '<a class="ft-a-title" rel="nofollow" href="'+Label.servePath+list[i].articlePermalink+'">'+list[i].articleTitleEmoj+'</a>'+
                                                (list[i].articlePerfect == 1 ? '<span class="perfectIcon"></span>' : '')+
                                            '</h3>'+
                                    
                                            '<span class="articleH4">'+
                                                '<a href="'+Label.servePath+list[i].articlePermalink+'">'+list[i].articlePreviewContent+'</a>'+
                                            '</span>'+
                                            '<div class="fn-clear ft-smaller list-info"></div>'+
                                        
                                            '<div class="fn-flex">'+
                                                '<a  rel="tag" href="'+Label.servePath+'/tag/'+list[i].articleTagObjs[0].tagURI+'" class="articleTagBtn">'+list[i].articleTagObjs[0].tagTitle+'</a>'+
                                                (list[i].articleStatus == 0 && Label.userRoleName!= null && Label.userRoleName.trim().length > 0 && (Label.userRoleName.indexOf(list[i].articleTagObjs[0].tagTitle) != -1)?
								        		'&nbsp;&nbsp;&nbsp;<span class="articleTagBtn" onclick="Util.sendArticleToWechat('+list[i].oId+')" style=" color: #fff !important;background: #007D99;cursor: pointer;">推送</span>' : '')+
								        		'<div class="articleAction">'+
                                                '<span class="fn-right ft-fade">'+
                                                    '<a class="ft-fade" href="'+Label.servePath+list[i].articlePermalink+'">'+
                                                        '<span class="article-level'+( list[i].articleViewCount<400?(parseInt(list[i].articleViewCount)/100):4)+' mobileView">'+list[i].articleViewCount+'</span></a>'+
                                                    '&nbsp;&nbsp;&nbsp;'+
                                                    '<a class="ft-fade" href="'+Label.servePath+list[i].articlePermalink+'#comments"><span class="article-level'+( list[i].articleCommentCount<40?(parseInt(list[i].articleCommentCount)/10):4)+' mobileChat">'+list[i].articleCommentCount+'</span></a>'+
                                                '</span>'+
                                            '<div>'+
                                        '</div>'+
                                    '</div>'+
                                    (list[i].articleThumbnailURL != "" ? '<a href="'+Label.servePath+list[i].articlePermalink+'" class="abstract-img" style="background-image:url('+list[i].articleThumbnailURL+')"></a>' : '')+
                                '</div>'+
                                (list[i].articleStick > 0 ? '<span class="cb-stick tooltipped tooltipped-e" aria-label="'+ (list[i].articleStick<9223372036854775807?Label.stickLabel+Label.remainsLabel+list[i].articleStickRemains+Label.minuteLabel:Label.adminLabel+Label.stickLabel) +'"><span class="icon-pin"></span></span>' : ''));
                        $ul.append($li);
                    }
                    if(sessionStorage.indexPage && x==0){
                        $(window).scrollTop(parseInt(sessionStorage.indexTop));
                        page=parseInt(sessionStorage.indexPage);
                    }
                    sessionStorage.indexPage=page;
                    $('#clickMore').html('加载更多').attr('flag','true');
                }
            }
        });
    }
    takeArticles(page,0);
</script>