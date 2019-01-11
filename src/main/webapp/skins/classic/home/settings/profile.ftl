<#include "macro-settings.ftl">
<@home "profile">
<div class="module">
    <div class="module-header fn-clear">
        <a rel="nofollow" href="${servePath}/member/${currentUser.userName}" target="_blank">${currentUser.userNickname}(${currentUser.userName})</a>
        <h2>${profilesLabel}</h2>
        <span>(${currentUser.userEmail})</span>
    </div>
    <div class="module-panel form fn-clear">
    
    	<#if currentUser.userName == "admin">
	    	<label>${nicknameLabel}</label><br/>
	        <input id="userNickname" type="text" value="${currentUser.userNickname}" placeholder="${selfNicknameLabel}"/>
        <#else>
         	<input id="userNickname" type="hidden" value="${currentUser.userNickname}" placeholder="${selfNicknameLabel}"/>
		</#if>
        <#--<label>${selfTagLabel}</label><br/>-->
        <input id="userTags" type="hidden" value="${currentUser.userTags}" placeholder="${selfDescriptionLabel}"/>

        <#--<label>URL</label><br/>-->
        <input id="userURL" type="hidden" value="${currentUser.userURL}" placeholder="${selfURLLabel}"/>

        <label>${userIntroLabel}</label><br/>
        <textarea id="userIntro" placeholder="${selfIntroLabel}">${currentUser.userIntro}</textarea>
        <br>
        <br>
        <div class="tip" id="profilesTip"></div>
        <br>
        <button class="fn-right blue" onclick="Settings.update('profiles', '${csrfToken}')">${saveLabel}</button>
        <button class="blue" onclick="Settings.preview(this)">${previewLabel}</button>
    </div>
</div>
</@home>