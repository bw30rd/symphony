<div class="side-center" >
    <div id="avatarURLDom" class="avatar midSize" title="${user.userName}" style="background-image:url('${user.userAvatarURL48}')"></div>
    <div>
        <div class="user-name">
            <div id="userNicknameDom"><b>${user.userNickname}</b><div class="ft-gray">${user.userName}</div></div>
            <div class="side-userRank">
                <span class="${user.userLevelType}" aria-label="等级"> ${user.userLevel}</span>
                <span class="ft-gray">${pointLabel}</span>
	           <#if isLoggedIn && (currentUser.userName == user.userName)> 
	            <a onclick="window.location.href ='${servePath}/member/${user.userName}/points'" title="${user.userPoint?c}">
	                <#if 0 == user.userAppRole>
	                ${user.userPoint?c}
	                <#else>
	                <div class="painter-point" style="background-color: #${user.userPointCC}"></div>
	                </#if>
	            </a>
                <#else>
	            <span aria-label="${user.userPoint?c}">${user.userPoint?c}</span>
                </#if>
				<#if permissions["userAddPoint"].permissionGrant ||
                        permissions["userAddUser"].permissionGrant ||
                        permissions["userExchangePoint"].permissionGrant ||
                        permissions["userDeductPoint"].permissionGrant ||
                        permissions["userUpdateUserAdvanced"].permissionGrant ||
                        permissions["userUpdateUserBasic"].permissionGrant>
                    <a class="ft-13 ft-a-title" href="${servePath}/admin/user/${user.oId}"><span class="icon-setting"></span></a>
                </#if>
            </div>

			<#if user.userIntro!="">
	        <div class="user-intro" id="userIntroDom" >
	            ${user.userIntro}
	        </div>
	        </#if>

			<#if isLoggedIn && (currentUser.userName != user.userName)>
                <#if isFollowing>
                    <button class="followed" onclick="Util.unfollow(this, '${followingId}', 'user')">
                        ${unfollowLabel}
                    </button>
                    <#else>
                    <button class="follow" onclick="Util.follow(this, '${followingId}', 'user')">
                        ${followLabel}
                    </button>
                </#if>
			<#elseif isLoggedIn>
				<#if !isDailyCheckin>
		         <#--  <button class="ft-button daySign submitGreenBtn" onclick="window.location.href = 
		         '<#if useCaptchaCheckin>${servePath}/activity/checkin<#else>${servePath}/activity/daily-checkin</#if>?checkinType=phone'">${dailyCheckinLabel}</button>
		          -->
		          <button class="ft-button daySign submitGreenBtn" onclick=" 
         		<#if useCaptchaCheckin>window.location.href ='${servePath}/activity/checkin'<#else>Activity.checkin('${csrfToken}', 'phone')</#if>">${dailyCheckinLabel}</button>
        
		          
		        <#else>
		            
		        <button class="ft-button daySign3 backWhiteBtn backWhiteBtn__grey">${checkinStreakLabel} ${currentUser.userCurrentCheckinStreak} 天</button>
		
		     	</#if>
				<button onclick="window.location.href ='${servePath}/settings'" class="backWhiteBtn">设置</button>
            </#if>
			<ul class="status fn-flex ulzq">
				<li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${user.userName}/followers'">
                    <span class="ft-gray">粉丝</span>
                    <strong style="font-size:14px; color:hsl(191, 100%, 30%);">${followerCnt?c}</strong>
	            </li>
	            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${user.userName}/following/users'">
                    <span class="ft-gray">Ta的关注</span>
                    <strong style="font-size:14px; color:hsl(191, 100%, 30%);">${followingUserCnt?c}</strong
	            </li>
			</ur>

        </div>
        
        <#--<div class="user-info">
           <span class="ft-gray">个人邮箱:</span> ${user.userEmail}
        </div>-->
        
       <#-- <#if "" != user.userTags>
        <div class="user-info">
            <span class="ft-gray">${selfTagLabel}</span>
            <span id='userTagsDom'>
                <#list user.userTags?split(',') as tag> ${tag?html}<#if tag_has_next>,</#if></#list>
            </span>
        </div>
        </#if>
        <#if "" != user.userCity && 0 == user.userGeoStatus>
        <div class="user-info">
            <span class="ft-gray">${geoLabel}</span> <#if "中国" == user.userCountry>${user.userCity}<#else>${user.userCountry} ${user.userCity}</#if>
        </div>
        </#if>
        <div class="user-info">
            
        </div>
        <#if user.userURL!="">
        <div class="user-info">
            <a id="userURLDom" target="_blank" rel="friend" href="${user.userURL?html}">${user.userURL?html}</a>
        </div>
        </#if> -->
        
        <#--<div class="user-info">
            <span class="ft-gray">${joinTimeLabel}</span> ${user.userCreateTime?string('yyyy-MM-dd HH:mm:ss')}
        </div>-->
        
       <#-- <div class="user-info">
            <span class="ft-gray">${checkinStreakPart0Label}</span>
            ${user.userLongestCheckinStreak?c} 
            <span class="ft-gray">${checkinStreakPart1Label}</span> 
            ${user.userCurrentCheckinStreak?c}
            <span class="ft-gray">${checkinStreakPart2Label}</span>
        </div>-->

        <#--<div class="fn-hr10"></div>
        <ul class="status fn-flex">
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${user.userName}'">
                <strong>${user.userArticleCount?c}</strong>
                <span class="ft-gray">${articleLabel}</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${user.userName}/comments'">
                <strong>${user.userCommentCount?c}</strong>
                <span class="ft-gray">回帖</span>
            </li>
            <li class="fn-pointer" onclick="window.location.href = '${servePath}/member/${user.userName}/following/articles'">
                <strong>${followingArticleCnt?c}</strong>
                <span class="ft-gray">收藏</span>
            </li>
        </ul>
    	</div>-->
</div>

<script src="${staticServePath}/js/activity.js?${staticResourceVersion}"></script>