<#include "macro-top.ftl">
<#include "../macro-pagination.ftl">
<@top "usersRank">
<h2 class="sub-head">
    <span class="ft-red" style="color:#000;">♠</span> 等级排行
</h2>
<div class="list">
    <ul class="userRankListLeft">
        <#list topBalanceUsers as user>
        <li>
            <div class="t01"><a class="rightRank rightRank${user_index + (paginationCurrentPageNum-1)*pageSize}">${user_index + 1+ (paginationCurrentPageNum-1)*20}</a></div>
            <div class="t03">
                <a href="${servePath}/member/${user.userName}"><div class="avatar" style="background-image:url('${user.userAvatarURL}')"></div></a>
            </div>
            <div class="t04"><a href="${servePath}/member/${user.userName}">${user.userNickname}</a></div>
            <div class="t05"><a class='${user.userLevelType}'>${user.userLevel}</a></div>
            <div class="t06">${user.userPoint?c}</div>
            <div class="t07">帖子 ${user.userArticleCount}&nbsp; 回帖 ${user.userCommentCount}&nbsp; 粉丝 ${user.followerCnt}</div>
            <div class="t02">
                <#if isLoggedIn && (currentUser.userName != user.userName)>
                    <#if user.isFollowing ?? && user.isFollowing>
                        <button class="followed" onclick="Util.unfollow(this, '${user.oId}', 'user')">${unfollowLabel}</button>
                    <#else>
                        <button class="follow" onclick="Util.follow(this, '${user.oId}', 'user')">${followLabel}</button>
                    </#if>
                </#if>
            </div>
        </li>
        </#list>
    </ul>
    <@pagination url="${servePath}/top/usersRank"/>
</div>
</@top>