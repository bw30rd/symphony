<#include "macro-home.ftl">
<#include "../macro-pagination.ftl">
<@home "${type}">
<#if 0 == user.userFollowingUserStatus || (isLoggedIn && ("adminRole" == currentUser.userRole || currentUser.userName == user.userName))>
<div class="follow">
    <ol>
        <#list userHomeFollowingUsers as followingUser>
        <li class="fn-flex">
            <a rel="nofollow" title="${followingUser.userName} <#if followingUser.userOnlineFlag>${onlineLabel}<#else>${offlineLabel}</#if>" href="${servePath}/member/${followingUser.userName}">
                <div class="avatar fn-left" style="background-image:url('${followingUser.userAvatarURL48}')"></div>
            </a>
            <div class="fn-flex-1">
                <h3 class="fn-inline">
                    <a rel="nofollow" href="${servePath}/member/${followingUser.userName}" ><#if followingUser.userNickname != ''>${followingUser.userNickname}<#else>${followingUser.userName}</#if></a>
                	<a class="${followingUser.userLevelType}">${followingUser.userLevel}</a>
                </h3> &nbsp;
                <#if isLoggedIn && (currentUser.userName != followingUser.userName)>
                <#if followingUser.isFollowing>
                <button class="small fn-right followed" onclick="Util.unfollow(this, '${followingUser.oId}', 'user')"> 
                    ${unfollowLabel}
                </button>
                <#else>
                <button class="blue small fn-right follow" onclick="Util.follow(this, '${followingUser.oId}', 'user')"> 
                    ${followLabel}
                </button>
                </#if>
                </#if>
                <div>
                    <#--<#if followingUser.userArticleCount == 0>
                    <#if followingUser.userURL != "">
                    <a class="ft-gray" target="_blank" rel="friend" href="${followingUser.userURL?html}">${followingUser.userURL?html}</a>
                    <#else>
                    <span class="ft-gray">${symphonyLabel}</span>
                    ${followingUser.userNo?c}
                    <span class="ft-gray">${numVIPLabel}</span>
                    </#if>
                    <#else>
                    <span class="ft-gray">${articleLabel}</span> ${followingUser.userArticleCount?c} &nbsp;
                    <span class="ft-gray">${tagLabel}</span> ${followingUser.userTagCount?c}
                    </#if>-->
					<span class="ft-gray">${articleLabel}</span> ${followingUser.userArticleCount?c} &nbsp;
                    <span class="ft-gray">${followersLabel}</span> ${followingUser.followerCnt?c}
                       
                </div>
            </div>
        </li>
        </#list>
    </ol>
</div>
<@pagination url="${servePath}/member/${user.userName}/following/users"/>
<#else>
<p class="ft-center ft-gray home-invisible">${setinvisibleLabel}</p>
</#if>
</@home>