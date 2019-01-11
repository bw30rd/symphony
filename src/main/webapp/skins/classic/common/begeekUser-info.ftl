<#if isLoggedIn>

<div class="module person-info" >
    <div class="module-panel " aria-label="当前用户：${currentUser.userNickname}">
        <ul class="status fn-flex">
			<li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/points'">
                <strong id="uPoint2">${currentUser.userPoint?c}</strong>
                <span class="ft-gray userIcon1">积分</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/following/users'">
                <strong>${currentUser.followingUserCnt?c}</strong>
                <span class="ft-gray userIcon2">${followingUsersLabel}</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${currentUser.userName}/following/articles'">
                <strong>${currentUser.followingArticleCnt?c}</strong>
                <span class="ft-gray userIcon3">${followingArticlesLabel}</span>
            </li>
        	
        </ul>
        <#if !isDailyCheckin>
        <#--  <button class="ft-button daySign" onclick="window.location.href = 
         '<#if useCaptchaCheckin>${servePath}/activity/checkin<#else>${servePath}/activity/daily-checkin</#if>'">${dailyCheckinLabel}</button>
        -->
         <button id="signBtn" class="ft-button daySign" onclick=" 
         <#if useCaptchaCheckin>window.location.href ='${servePath}/activity/checkin'<#else>Activity.checkin('${csrfToken}', 'pc')</#if>">${dailyCheckinLabel}</button>
         <p id="plusCent">+</p>
         
        <#-- <#else><button class="ft-button daySign3">${checkinStreakLabel} ${currentUser.userCurrentCheckinStreak} 天</button> -->

     	</#if>
        
        <div class="fn-clear userInfo_zq" >
           <#-- <span class="ft-red">♠</span> <a href="${servePath}/top/balance">${wealthRankLabel}</a>-->
            
            <span class="ft-green">♠</span> <a onclick="Begeek.getUserRank();" class="">等级排行</a>
            <span class="ft-red">♠</span> <a onclick="Begeek.getUserLiveness();">活跃度排行</a>
    	</div>
    </div> 
    <div class="top-left activity-board"></div>
    <div class="top-right activity-board"></div>
    <div class="right activity-board"></div>
    <div class="bottom activity-board"></div>
    <div class="left activity-board"></div>
   
</div>

<div class="list" id="userRankList" style="display:none">
	<div class="option userLife_zq" style="display:none">
		<a style="cursor:pointer;text-decoration:none;" class="week" onclick="Begeek.getWeekLiveness(this)">本周</a><a style="cursor:pointer;text-decoration:none;" class="moon" onclick="Begeek.getMoonLiveness(this)">本月</a>
	</div>
	<div class="userTeam_zq" >
    	<span>排行</span><span>用户</span><span>等级</span><span>积分</span>
    </div>
    <ul id="userRank" ></ul>
</div>

<div class="module person-info" id="userInfo" >
	<ul class="userInfoList_zq">
        <li>
            <div class="progressOut_zq"><div class="progressIn_zq"></div></div><p class="progressText_zq"><span id="upCount">${currentUser.userPoint?c}</span>/<font id="downCount">${currentUser.userPoint?number + upgradePoints?number}</font></p>
		</li>
        <li>
            <div class="pointEx_zq">
                <span class="goLeft_zq">积分<strong id="uPoint">${currentUser.userPoint?c}</strong></span>
                <span>差<strong id="uupgradePoints">${upgradePoints}</strong>分升级</span>
                <span> <a onmouseover="points_show()" onmouseout="points_show()" class="quesIcon"></a></span>
            </div>
            <#include "explanation/points.ftl">
        </li>
        <#if !isDailyCheckin>
        <li id="signLi" style="display:none;"></li>
        <#else>
        <li id="signLi" style="display:block;">已连续签到 <span style="color:#FF9933">${currentUser.userCurrentCheckinStreak}</span> 天</li>
     	</#if>
        <li>
            <span >我的等级</span><strong class="glory_bronze3"><a style="cursor: pointer;" class="${currentUserLevelType}" onmouseover="userLevel_show()" onmouseout="userLevel_show()" id="ucurrentUserLevel">${currentUserLevel}</a></strong>
            <#include "explanation/userLevel.ftl">
        </li> 
		<li><a style="color:#666; text-decoration:none;cursor:pointer;" onclick="Begeek.getUserRank();">等级排名  第<strong id="uRank">${userRank}</strong>名</a></li>
	</ul>
</div>

<script src="${staticServePath}/js/begeek${miniPostfix}.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/activity.js?${staticResourceVersion}"></script>

<script type="text/javascript">

function points_show(){
    $("#points_explanation").stop().slideToggle();
}

function userLevel_show(){
	$("#userLevel_explanation").stop().slideToggle();
}
</script>

</#if>