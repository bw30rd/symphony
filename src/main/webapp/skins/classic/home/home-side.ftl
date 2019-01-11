<div class="ft-center module">
    <div id="avatarURLDom" class="avatar-big" style="background-image:url('${user.userAvatarURL210}')" 
    	onclick=""></div>
    <div>
        <div class="user-name">
            <div id="userNicknameDom"><b>${user.userNickname}</b></div>
            <div class="ft-gray">${user.userName}</div>

            <div>
            
               <#-- <#if isLoggedIn && (currentUser.userName != user.userName)>
                    <button class="green small" onclick="location.href = '${servePath}/post?type=1&at=${user.userName}'">
                        ${privateMessageLabel}
                    </button>
                </#if>
                <#if (isLoggedIn && ("adminRole" == currentUser.userRole || currentUser.userName == user.userName)) || 0 == user.userOnlineStatus>
                    <span class="tooltipped tooltipped-n" aria-label="<#if user.userOnlineFlag>${onlineLabel}<#else>${offlineLabel}</#if>">
                        <span class="<#if user.userOnlineFlag>online<#else>offline</#if>"><img src="${staticServePath}/images/on-line.png" /></span>
                    </span>
                </#if>-->
                
                <span class="tooltipped tooltipped-n offline nobg" aria-label="等级"><a class="${user.userLevelType}">${user.userLevel}</a></span>
                
                <span class="ft-gray">${pointLabel}</span>
                <#if isLoggedIn && (currentUser.userName == user.userName)>
	        	<a href="${servePath}/member/${user.userName}/points" class="tooltipped tooltipped-n" aria-label="${user.userPoint?c}">
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
                    <a class="ft-13 tooltipped tooltipped-n ft-a-title" href="${servePath}/admin/user/${user.oId}" aria-label="${adminLabel}"><span class="icon-setting"></span></a>
                </#if>
            </div>
            
            <#if isLoggedIn && (currentUser.userName != user.userName)>
            <#if isFollowing>
            <button class="followed" onclick="Util.unfollow(this, '${followingId}', 'user')"> 
                ${unfollowLabel}
            </button>
            <#else>
            <button class="follow"  onclick="Util.follow(this, '${followingId}', 'user')"> 
                ${followLabel}
            </button>
            </#if>
            </#if>
        </div>
        
        <div class="user-details">
        <#if user.userIntro!="">
        <div class="user-intro" id="userIntroDom">
           <span class="ft-gray">个性签名：</span> ${user.userIntro}
        </div>
        </#if>
        <div class="user-info">
        	<#if isLoggedIn><span class="ft-gray">个人邮箱:</span> ${user.userEmail}<#else></#if>
        </div>
        
      <#--  <#if "" != user.userTags>
        <div class="user-info">
            <span class="ft-gray">${selfTagLabel}</span> 
            <span id="userTagsDom"><#list user.userTags?split(',') as tag> ${tag?html}<#if tag_has_next>,</#if></#list></span>
        </div>
        </#if> 

        <#if "" != user.userCity && 0 == user.userGeoStatus>
        <div class="user-info">
            <span class="ft-gray">${geoLabel}</span> <#if "中国" == user.userCountry>${user.userCity}<#else>${user.userCountry} ${user.userCity}</#if>
        </div>
        </#if>
        <#if user.userURL!="">
        <div class="user-info">
            <a id="userURLDom" target="_blank" rel="friend" href="${user.userURL?html}">${user.userURL?html}</a>
        </div>
        </#if>-->

        <div class="user-info">
            <span class="ft-gray">${joinTimeLabel}:</span> ${user.userCreateTime?string('yyyy-MM-dd HH:mm')}
        </div>
        
      <#--  <div class="user-info">
            <span class="ft-gray">${checkinStreakPart0Label}</span>
            ${user.userLongestCheckinStreak?c} 
            <span class="ft-gray">${checkinStreakPart1Label}</span> 
            ${user.userCurrentCheckinStreak?c}
            <span class="ft-gray">${checkinStreakPart2Label}</span>
        </div>-->
        </div>
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
    </div>
</div>


<script type="text/javascript">
function avatar_show(){
    $(".avatar-module").show();
}


</script>