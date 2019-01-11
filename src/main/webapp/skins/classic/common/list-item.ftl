<li>
    <div class="fn-flex">
    	<#if "" != article.articleThumbnailURL>
            <a href="${servePath}${article.articlePermalink}" class="abstract-img" style="background-image:url('${article.articleThumbnailURL}')"></a>
        </#if>
        
        <div class="fn-flex-1">
        	<div class="fn-clear ft-smaller list-info torb">
        		<#if article.articleStatus?? && article.articleStatus == 0>
        		<#if (tag ?? && tag.isDomainAdmin) || 
        			(userRoleName?? && userRoleName?contains(article.articleTagObjs[0].tagTitle))>
        		<span class="tag-follow" onclick="Util.sendArticleToWechat('${article.oId}')" style="cursor: pointer;">推送</span>
        		</#if>
        		</#if>
        		&nbsp;&nbsp;
                <span class="fn-right ft-fade">
                	${article.articleCreateTime?string('yyyy-MM-dd')}&nbsp;•&nbsp;
					<#if article.articleViewCount != 0>
		                <a class="ft-fade" href="${servePath}${article.articlePermalink}"><span class="article-level<#if article.articleViewCount lt 400>${(article.articleViewCount/100)?int}<#else>4</#if>">${article.articleViewCount}</span> ${viewLabel}</a>
		            </#if>            
					<#if article.articleCommentCount != 0>
		              &nbsp;•&nbsp;<a class="ft-fade" href="${servePath}${article.articlePermalink}#comments"><b class="article-level<#if article.articleCommentCount lt 40>${(article.articleCommentCount/10)?int}<#else>4</#if>">${article.articleCommentCount}</b> ${cmtLabel}</a> 
		            </#if>
                </span>
                <#list article.articleTagObjs as articleTag>
		        	<a class="fn-left ft-fade pcArticleTag toRightZq" rel="tag" href="${servePath}/tag/${articleTag.tagURI}">#${articleTag.tagTitle}</a>
		        </#list> 
	        </div>
	        
            <div class="fn-flex">
            	<#if article.articleAnonymous == 1 && (!isLoggedIn || article.articleAuthorId != currentUser.oId)>
            		<div class="avatar" style="background-image:url('${article.articleAuthorThumbnailURL48}')"></div>
            	<#else>
	                <#if article.articleAnonymous == 1 && isLoggedIn && article.articleAuthorId == currentUser.oId>
	            		<a rel="nofollow" href="${servePath}/member/${currentUser.userName}">
	            		<div class="avatar" style="background-image:url('${currentUser.userAvatarURL48}')"></div></a>
	            	<#else>
	            		<a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
	            		<div class="avatar" style="background-image:url('${article.articleAuthorThumbnailURL48}')"></div></a>
	           		</#if>
           		</#if>
           		
               <#-- <#if article.articleAnonymous == 0>
                    <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}"></#if><div
                    class="avatar"
                    style="background-image:url('${article.articleAuthorThumbnailURL48}')"></div><#if article.articleAnonymous == 0></a></#if> --> 

                <div class="fn-ellipsis ft-fade ft-smaller list-info">
                    <#if article.articleAnonymous == 1 && (!isLoggedIn || article.articleAuthorId != currentUser.oId)>
                  	  	匿名用户
	            	<#else>
		                <#if article.articleAnonymous == 1 && isLoggedIn && article.articleAuthorId == currentUser.oId>
			                <a rel="nofollow" class="author" href="${servePath}/member/${article.articleAuthorName}">
			                	${article.articleAuthor.userNickname}（已匿名）</a>
		            	<#else>
		            		<a rel="nofollow" class="author" href="${servePath}/member/${article.articleAuthorName}">
		            			${article.articleAuthor.userNickname}</a>
		           		</#if>
			            <a class="${article.articleAuthor.userLevelType}">${article.articleAuthor.userLevel}</a>
		           		<br>
		           		<#if article.articleAuthor.userIntro != '' >
                        	${article.articleAuthor.userIntro}
                      	</#if>
	           		</#if>
                    
                    <#-- <#if article.articleAnonymous == 0>
                        <a rel="nofollow" class="author"
                           href="${servePath}/member/${article.articleAuthorName}"></#if>
                    ${article.articleAuthor.userNickname} 
                    <#if article.articleAnonymous == 0></a></#if>
					<a class="${article.articleAuthor.userLevelType}">${article.articleAuthor.userLevel}</a>
					<br>
                    <#if article.articleAuthor.userIntro != '' && article.articleAnonymous == 0>
                        ${article.articleAuthor.userIntro}
                    </#if> -->
                    
                </div>
            </div>
		
			<h2 class="moreShort">
				<#if 1 == article.articlePerfect>
                    <!-- <span class="tooltipped tooltipped-w" aria-label="${perfectLabel}"><svg height="20" width="14" viewBox="3 2 11 12">${perfectIcon}</svg></span> -->
                    <a class="prefectIcon"></a>
		        </#if>
		        <#if 1 == article.articleType>
		            <span class="tooltipped tooltipped-w" aria-label="${discussionLabel}"><span class="icon-locked"></span></span>
		            <#elseif 2 == article.articleType>
		                <span class="tooltipped tooltipped-w" aria-label="${cityBroadcastLabel}"><span class="icon-feed"></span></span>
		                <#elseif 3 == article.articleType>
		                    <span class="tooltipped tooltipped-w" aria-label="${thoughtLabel}"><span class="icon-video"></span></span>
		        </#if>
		        <a class="ft-a-title" data-id="${article.oId}" data-type="${article.articleType}" rel="bookmark" href="${servePath}${article.articlePermalink}"><p>${article.articleTitleEmoj}</p></a>
    		</h2>
    		
            <a class="abstract" href="${servePath}${article.articlePermalink}">
                <p>${article.articlePreviewContent}</p>
            </a>
        </div>
        
    </div>
	
	

    <#--<span class="heat tooltipped tooltipped-n" aria-label="${postActivityLabel}" style="width:${article.articleHeat*3}px"></span>-->

    <#if article.articleStick gt 0>
        <span class="cb-stick tooltipped tooltipped-e" aria-label="<#if article.articleStick < 9223372036854775807>${stickLabel}${remainsLabel} ${article.articleStickRemains?c} ${minuteLabel}<#else>${stickLabel}</#if>"><span class="icon-pin"></span></span>
    </#if>
</li>