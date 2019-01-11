<#include "macro-head.ftl">
<#include "macro-list.ftl">
<#include "macro-pagination.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${symphonyLabel}">
        <meta name="description" content="${symDescriptionLabel}"/>
        </@head>
        <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
        <link rel="canonical" href="${servePath}">
    </head>
    <body class="index">
        ${HeaderBannerLabel}
        <#include "header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="index-main">
                    <#if isLoggedIn>
                        <a href="${servePath}/post?type=0" class="tooltipped tooltipped-w radio-btn-addFile" aria-label="发帖"></a>
                    </#if>
                    <div class="index-tabs fn-flex tab_zq" id="articles">
                        <span class="current" data-index="0">
                            <span class="icon-00"></span> ${latestLabel}
                        </span>
                        <#if isLoggedIn>
                        <span class="watch" data-index="1">
                            <span class="icon-11"></span>
                            ${watchingArticlesLabel}
                        </span>
                        </#if>
                        <span class="perfect" data-index="2">
                        	<span class="icon-22"></span>
                            ${perfectLabel}
                        </span>
                        <#if isLoggedIn>
                        <span class="tags" data-index="3">
                            <span class="icon-33"></span>
                            ${followingTagsLabel}
                        </span>
                        </#if>
                    </div>
					
                    <div class="index-tabs-panels list article-list">
                        <ul>
                            <#list recentArticles as article>
                                <#include "common/list-item.ftl">
                            </#list>
                            <#if recentArticles?size == 0>
                                ${systemEmptyLabel}<br>
                                ${systemEmptyTipLabel}<br> 
                                <img src="${staticServePath}/images/404/5.gif"/>          
                            </#if>
                            <li>
                                <a class="more" href="${servePath}/recent">${moreRecentArticleLabel}</a>
                            </li>
                        </ul>
                        
                        <ul class="fn-none">
                            <#list followingArticles as article>
                                <#include "common/list-item.ftl">
                            </#list>
                            <#if isLoggedIn && followingArticles?size == 0>
                                <li class="ft-center">
                                    ${noFollowingArticleLabel}<br>
                                 <#--   ${noFollowingTagTipLabel}<br> -->
                                    <img src="${staticServePath}/images/404/6.gif"/>     
                                </li>  
                            </#if>
                            <#if !isLoggedIn>
                                <li class="ft-center">
                                    ${noLoginLabel}<br>
                                    ${noLoginTipLabel}<br> 
                                    <img src="${staticServePath}/images/404/6.gif"/>     
                                </li>  
                            </#if>
                            <li>
                                <a class="more" href="${servePath}/recent">${moreRecentArticleLabel}</a>
                            </li>
                        </ul>
                        
                        <ul class="fn-none">
                            <#list perfectArticles as article>
                            	<#include "common/list-item.ftl">
                            	
                           <#--<li>
                                <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se" aria-label="${article.articleAuthorName}" style="background-image:url('${article.articleAuthorThumbnailURL48}')"></span>
                                </a>
                                <a rel="nofollow" class="fn-ellipsis ft-a-title" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}">${article.articleViewCount}</a>
                            </li>-->
                            </#list>
                            <#if perfectArticles?size == 0>
                                <li>${chickenEggLabel}</li>
                            </#if>
                        </ul>
                        
                        <ul class="fn-none">
                            <#list followingTagArticles as article>
                                <#include "common/list-item.ftl">
                            </#list>
                            <#if isLoggedIn && followingTagArticles?size == 0>
                                <li class="ft-center">
                                    ${noFollowingTagLabel}<br>
                                    ${noFollowingTagTipLabel}<br> 
                                    <img src="${staticServePath}/images/404/6.gif"/>     
                                </li>  
                            </#if>
                            <#if !isLoggedIn>
                                <li class="ft-center">
                                    ${noLoginLabel}<br>
                                    ${noLoginTipLabel}<br> 
                                    <img src="${staticServePath}/images/404/6.gif"/>     
                                </li>  
                            </#if>
                            <li>
                                <a class="more" href="${servePath}/recent">${moreRecentArticleLabel}</a>
                            </li>
                        </ul>
                        
                    </div>
                </div>
                
                
                <#if isLoggedIn>
                <div class="index-side">
               	 	<#include "common/begeekUser-info.ftl">
                </div>
                </#if>

            </div>
        </div>  
         
    <#include "footer.ftl">   
    <!-- <#if isLoggedIn>
        <a href="${servePath}/post?type=0" class="tooltipped tooltipped-w radio-btn-addFile" aria-label="发帖"></a>
    </#if> -->
    
    <script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
    <script src="${staticServePath}/js/begeek${miniPostfix}.js?${staticResourceVersion}"></script>
    <script type="text/javascript">
        $('.metro-item').height($('.metro-item').width());
        $('.timeline ul').outerHeight($('.metro-item').width() * 2 + 2);

        // tab
        $('#articles span').click(function () {
            var $it = $(this);
            $('#articles span').removeClass('current');
            $it.addClass('current');

            $(".index-tabs-panels.article-list ul").hide();
            if ($it.hasClass('watch')) {
                $(".index-tabs-panels.article-list ul:eq(1)").show();
            } else if ($it.hasClass('perfect')) {
                $(".index-tabs-panels.article-list ul:eq(2)").show();
            } else if ($it.hasClass('tags')){
                $(".index-tabs-panels.article-list ul:eq(3)").show();
            } else {
                $(".index-tabs-panels.article-list ul:eq(0)").show();
            }

            localStorage.setItem('indexTab', $it.data('index'));
        });

        // tag click
        $('.preview, .index-tabs > span').click(function (event) {
            var $it = $(this),
            maxLen = Math.max($it.width(), $it.height());
            $it.prepend('<span class="ripple" style="top: ' + (event.offsetY - $it.height() / 2)
                + 'px;left:' + (event.offsetX - $it.width() / 2) + 'px;height:' + maxLen + 'px;width:' + maxLen + 'px"></span>');

            setTimeout(function () {
                $it.find('.ripple').remove();
            }, 800);
        });

        // set tab
        if (typeof(localStorage.indexTab) === 'string') {
            $('.index-tabs:first > span:eq(' + localStorage.indexTab + ')').click();
        } else {
            localStorage.setItem('indexTab', 0);
        }
        
        $('.progressIn_zq').width(((parseInt($('#upCount').html())/parseInt($('#downCount').html())).toFixed(2)*100)+'%');
        
        // Init [Timeline] channel
        TimelineChannel.init("${wsScheme}://${serverHost}:${serverPort}${contextPath}/timeline-channel", 20);
        
        $('body').click(function () {
			$("#points_explanation").hide();
			$("#userLevel_explanation").hide();
		});
    </script>
</body> 
</html>
