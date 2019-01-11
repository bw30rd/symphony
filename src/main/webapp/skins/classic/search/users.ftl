<#include "macro-search.ftl">
<#include "../macro-pagination.ftl">
<@search "users">
<div class="content admin">
    <div class="module person-info">
        <form method="GET" action="${servePath}/search/users" class="form" accept-charset="UTF-8">
            <input name="key" type="text" placeholder="${userNameLabel}/用户昵称" value="<#if key??>${key}</#if>"/>
            <button type="submit" class="blue" >${searchLabel}</button>
        </form>
        <div class="list">
	        <ul class="userRankListLeft">
	        	<#if (users ??) && (users?size>0)>
	            <#list users as item>
	            <li>
	            	<div class="t03">
		                <a href="${servePath}/member/${item.userName}"><div class="avatar" style="background-image:url('${item.userAvatarURL}')"></div></a>
		            </div>
		            <div class="t04"><a href="${servePath}/member/${item.userName}">${item.userNickname}</a></div>
		            <div class="t05"><a class='${item.userLevelType}'>${item.userLevel}</a></div>
		            <div class="t08" >
			            <#if item.userIntro != '' >
			            	<span class="tooltipped tooltipped-n" aria-label="${item.userIntro}">
			            	<#if item.userIntro?length gt 17>  
							    ${item.userIntro?substring(0,15)}...  
							<#else>${item.userIntro!}   
							</#if> 	 
			            	</span>
			            </#if>
		            </div>
		            <div class="t07">帖子 ${item.userArticleCount}&nbsp; 回帖 ${item.userCommentCount}&nbsp; 粉丝 ${item.followerCnt}</div>
		            <div class="t02">
		                <#if isLoggedIn && (currentUser.userName != item.userName)>
		                    <#if item.isFollowing ?? && item.isFollowing>
		                        <button class="followed" onclick="Util.unfollow(this, '${item.oId}', 'user')">${unfollowLabel}</button>
		                    <#else>
		                        <button class="follow" onclick="Util.follow(this, '${item.oId}', 'user')">${followLabel}</button>
		                    </#if>
		                </#if>
		            </div>
	            </li>
	            </#list>
	            <#else>
	            <li><span>没有找到【<strong>${key}</strong>】相关用户</span></li>
	            </#if>
	        </ul>
	        <@pagination url="${servePath}/search/users"/>
	    </div>
    </div>
</div>
</@search>