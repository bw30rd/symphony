<li id="${comment.oId}"
    class="<#if comment.commentStatus == 1>cmt-shield</#if><#if comment.commentNice> cmt-perfect</#if><#if comment.commentReplyCnt != 0> cmt-selected</#if>">
    <div class="fn-flex">
        <#if !comment.fromClient>
            <div>
                <#-- <#if comment.commentAnonymous == 0>
                    <a rel="nofollow" href="${servePath}/member/${comment.commentAuthorName}"></#if>
                <div class="avatar tooltipped tooltipped-se"
                     aria-label="${comment.commentAuthorName}" style="background-image:url('${comment.commentAuthorThumbnailURL}')"></div>
                <#if comment.commentAnonymous == 0></a></#if> -->
                
                <#if comment.commentAnonymous == 1 && (!isLoggedIn || comment.commentAuthorId != currentUser.oId)>
	            	<div class="avatar tooltipped tooltipped-se"  aria-label="匿名用户" 
			                 style="background-image:url('${comment.commentAuthorThumbnailURL}')"></div>
	            <#else>
	                <#if comment.commentAnonymous == 1 && isLoggedIn && comment.commentAuthorId == currentUser.oId>
	                    <a rel="nofollow" href="${servePath}/member/${currentUser.userName}">
	                    <div class="avatar tooltipped tooltipped-se"  
	                    	aria-label="${currentUser.userName}" 
			                style="background-image:url('${currentUser.userAvatarURL48}')">
		                </div></a>
	                <#else>
	                    <a rel="nofollow" href="${servePath}/member/${comment.commentAuthorName}">
			            <div class="avatar tooltipped tooltipped-se"
			                 aria-label="${comment.commentAuthorName}" 
			                 style="background-image:url('${comment.commentAuthorThumbnailURL}')">
		                 </div></a>
	                </#if>
	            </#if>
            </div>
        <#else>
                <div class="avatar tooltipped tooltipped-se"
                     aria-label="${comment.commentAuthorName}" style="background-image:url('${comment.commentAuthorThumbnailURL}')"></div>
        </#if>
        <div class="fn-flex-1">
            <div class="comment-get-comment list"></div>
            <div class="fn-clear comment-info">
                <span class="fn-left ft-smaller">
                    <#if !comment.fromClient>
                    	<#-- <#if comment.commentAnonymous == 0>
                    		<a rel="nofollow" href="${servePath}/member/${comment.commentAuthorName}" class="ft-gray"></#if>
                    		<span class="ft-gray">${comment.commenter.userNickname}</span>
                    		<#if comment.commentAnonymous == 0></a></#if> -->
                    		
	                    <#if comment.commentAnonymous == 1 && (!isLoggedIn || comment.commentAuthorId != currentUser.oId)>
	                    	<span class="ft-gray">匿名用户</span>
	                    <#else>
		                    <#if comment.commentAnonymous == 1 && isLoggedIn && comment.commentAuthorId == currentUser.oId>
			                    <a rel="nofollow" href="${servePath}/member/${currentUser.userName}" class="ft-gray">
			                    	<span class="ft-gray">${comment.commenter.userNickname} (已匿名)</span>
			                    </a>
		                    <#else>
			                    <a rel="nofollow" href="${servePath}/member/${comment.commentAuthorName}" class="ft-gray">
			                    	<span class="ft-gray">${comment.commenter.userNickname}</span>
			                    </a>
		                    </#if>
	                    	<a class="${comment.commenter.userLevelType}">${comment.commenter.userLevel}</a>
	                    </#if>	
	                    
                    <#else>
                    	<span class="ft-gray">${comment.commenter.userNickname}</span>
						<a class="${comment.commenter.userLevelType}">${comment.commenter.userLevel}</a>
                    </#if>
                    <span class="ft-fade">• ${comment.timeAgo}</span>
                    
                    <#if 0 == comment.commenter.userUAStatus><span class="cmt-via ft-fade hover-show " data-ua="${comment.commentUA}"></span></#if>
                </span>
                <span class="fn-right">
                    <#if isLoggedIn && comment.commentAuthorName == currentUser.userName && permissions["commonRemoveComment"].permissionGrant>
                        <span onclick="Comment.remove('${comment.oId}')" aria-label="${removeCommentLabel}"
                              class="tooltipped tooltipped-n ft-a-title hover-show ">
                        <span class="icon-remove ft-red"></span></span>&nbsp;
                    </#if>
                    <#if permissions["commonViewCommentHistory"].permissionGrant>
                        <span onclick="Article.revision('${comment.oId}', 'comment')" aria-label="${historyLabel}"
                              class="tooltipped tooltipped-n ft-a-title hover-show 
                          <#if comment.commentRevisionCount &lt; 2>fn-none</#if>">
                        <span class="icon-history"></span></span> &nbsp;
                    </#if>
                    <#if isLoggedIn && comment.commentAuthorName == currentUser.userName && permissions["commonUpdateComment"].permissionGrant>
                        <span class="tooltipped tooltipped-n ft-a-title hover-show " onclick="Comment.edit('${comment.oId}'),reply_show()"
                           aria-label="${editLabel}"><span class="icon-edit"></span></span> &nbsp;
                    </#if>
                    <#if permissions["commentUpdateCommentBasic"].permissionGrant>
                    <a class="tooltipped tooltipped-n ft-a-title hover-show " href="${servePath}/<#if isArticleAdmin ?? && isArticleAdmin>domainAdmin<#else>admin</#if>/comment/${comment.oId}"
                       aria-label="${adminLabel}"><span class="icon-setting"></span></a> &nbsp;
                    </#if>
                    <#if comment.commentOriginalCommentId != ''>
                        <span class="fn-pointer ft-a-title tooltipped tooltipped-nw" aria-label="${goCommentLabel}"
                              onclick="Comment.showReply('${comment.commentOriginalCommentId}', this, 'comment-get-comment')"><span class="icon-reply-to"></span>
                        <div class="avatar-small" style="background-image:url('${comment.commentOriginalAuthorThumbnailURL}')"></div>
                    </span>
                    </#if>
                </span>
            </div>
            <div class="content-reset comment">
                ${comment.commentContent}
            </div>
            <div class="comment-action">
                <div class="ft-fade fn-clear">
                    <#if comment.commentReplyCnt != 0>
                        <span class="fn-pointer ft-smaller fn-left" onclick="Comment.showReply('${comment.oId}', this, 'comment-replies')">
                            ${comment.commentReplyCnt} ${replyLabel} <span class="icon-chevron-down"></span>
                        </span>
                    </#if>
                    <span class="fn-right hover-show action-btns">
                        <#assign hasRewarded = isLoggedIn && comment.commentAuthorId != currentUser.oId && comment.rewarded>
                        <span class="tooltipped tooltipped-n <#if hasRewarded>ft-red</#if>" aria-label="${thankLabel}"
                        <#if !hasRewarded && permissions["commonThankComment"].permissionGrant>
                            onclick="Comment.thank('${comment.oId}', '${csrfToken}', '${comment.commentThankLabel}', ${comment.commentAnonymous}, this)"
                        <#else>
                              <#-- onclick="Article.permissionTip(Label.noPermissionLabel)" -->
                        </#if>><span class="icon-heart"></span> ${comment.rewardedCnt}</span> &nbsp;
                    <span class="tooltipped tooltipped-n<#if isLoggedIn && 0 == comment.commentVote> ft-red</#if>"
                          aria-label="${upLabel}"
                    <#if permissions["commonGoodComment"].permissionGrant>
                          onclick="Article.voteUp('${comment.oId}', 'comment', this)"
                        <#else>
                            onclick="Article.permissionTip(Label.noPermissionLabel)"
                    </#if>><span class="icon-thumbs-up"></span> ${comment.commentGoodCnt}</span> &nbsp;
                    <span class="tooltipped tooltipped-n<#if isLoggedIn && 1 == comment.commentVote> ft-red</#if>"
                          aria-label="${downLabel}"
                    <#if permissions["commonBadComment"].permissionGrant>
                          onclick="Article.voteDown('${comment.oId}', 'comment', this)"
                        <#else>
                            onclick="Article.permissionTip(Label.noPermissionLabel)"
                    </#if>><span class="icon-thumbs-down"></span> ${comment.commentBadCnt}</span> &nbsp;
                    <#if isLoggedIn && comment.commentAuthorName != currentUser.userName && permissions["commonAddComment"].permissionGrant>
                        <span aria-label="${replyLabel}" class="icon-reply-btn tooltipped tooltipped-n"
                              onclick="reply_show(),Comment.reply('${comment.commentAuthorName}', '${comment.oId}')">
                        <span class="icon-reply"></span></span>
                    </#if>
                    </span>
                </div>
                <div class="comment-replies list"></div>
            </div>
        </div>
    </div>
</li>