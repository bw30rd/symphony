<#if isLoggedIn>
<div class="module person-info" data-percent="${liveness}">
    <div class="module-panel " aria-label="当前用户：${currentUser.userNickname}">
        <ul class="status fn-flex">
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/watching/articles'">
                <strong>${currentUser.watchingArticleCnt?c}</strong>
                <span class="ft-gray">${watchingArticlesLabel}</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/following/users'">
                <strong>${currentUser.followingUserCnt?c}</strong>
                <span class="ft-gray">${followingUsersLabel}</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/following/articles'">
                <strong>${currentUser.followingArticleCnt?c}</strong>
                <span class="ft-gray">${followingArticlesLabel}</span>
            </li>
        </ul>

        <div class="fn-clear" >
            <button class="ft-button daySign2" onclick="window.location.href = '${servePath}/post?type=0'">发帖</button>
        </div>
    </div> 
    <div class="top-left activity-board"></div>
    <div class="top-right activity-board"></div>
    <div class="right activity-board"></div>
    <div class="bottom activity-board"></div>
    <div class="left activity-board"></div>
</div>
</#if>