<#include "macro-settings.ftl">
<@home "profile">
<div class="module">
    <div class="module-header fn-clear">
        <a rel="nofollow" href="${servePath}/member/${currentUser.userName}" target="_blank" style="color:#007088;">${currentUser.userNickname}  ${currentUser.userName}</a>
        <#--<h2>${profilesLabel}</h2>-->
		<br>
        <span>邮箱：${currentUser.userEmail}</span>
        <#--<a class="ft-red fn-right" href="javascript:Util.logout()">${logoutLabel}</a>-->
    </div>
    <div class="module-panel form fn-clear">
    
    
 		<#if currentUser.userName == "admin">
	    	<label>${nicknameLabel}</label><br/>
	        <input id="userNickname" type="text" value="${currentUser.userNickname}" placeholder="${selfNicknameLabel}"/>
        <#else>
         	<input id="userNickname" type="hidden" value="${currentUser.userNickname}" placeholder="${selfNicknameLabel}"/>
		</#if>   
       <#-- <label>${selfTagLabel}</label><br/>
        <input id="userTags" type="text" value="${currentUser.userTags}" placeholder="${selfDescriptionLabel}"/>

        <label>URL</label><br/>
        <input id="userURL" type="text" value="${currentUser.userURL}" placeholder="${selfURLLabel}"/>-->
       
        <input id="userTags" type="hidden" value="${currentUser.userTags}" placeholder="${selfDescriptionLabel}"/>
        <input id="userURL" type="hidden" value="${currentUser.userURL}" placeholder="${selfURLLabel}"/>



        <label>${userIntroLabel}</label><br/>
        <textarea id="userIntro" placeholder="${selfIntroLabel}">${currentUser.userIntro}</textarea>
        <div class="fn-hr5"></div>
        <div class="fn-hr5"></div>
        <div class="tip" id="profilesTip"></div>
        <div class="fn-hr5"></div>
        <div class="fn-hr5"></div>
        <button class="blue fn-left submitGreenBtn" onclick="Settings.update('profiles', '${csrfToken}')">${saveLabel}</button>
        <button class="blue fn-right backWhiteBtn" onclick="javascript:history.back(-1);">返回</button>
    </div>
</div>
</@home>