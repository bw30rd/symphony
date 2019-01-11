<#macro home type>
<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <#if type == "home">
        <@head title="${articleLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="<#list userHomeArticles as article><#if article_index<3>${article.articleTitle},</#if></#list>"/>
        </@head>
        <#elseif type == "comments">
        <@head title="${cmtLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${cmtLabel}"/>
        </@head>
        <#elseif type == "followingUsers">
        <@head title="${followingUsersLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${followingUsersLabel}"/>
        </@head>
        <#elseif type == "followingTags">
        <@head title="${followingTagsLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${followingTagsLabel}"/>
        </@head>
        <#elseif type == "followingArticles">
        <@head title="${followingArticlesLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${followingArticlesLabel}"/>
        </@head>
        <#elseif type == "watchingArticles">
            <@head title="${watchingArticlesLabel} - ${user.userNickname} - ${symphonyLabel}">
            <meta name="description" content="${user.userNickname}${deLabel}${watchingArticlesLabel}"/>
        </@head>
        <#elseif type == "followers">
        <@head title="${followersLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${followersLabel}"/>
        </@head>
        <#elseif type == "points">
        <@head title="${pointLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${pointLabel}"/>
        </@head>
        <#elseif type == "settings">
        <@head title="${settingsLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${settingsLabel}"/>
        </@head>
        <#elseif type == "articlesAnonymous">
        <@head title="${anonymousArticleLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${anonymousArticleLabel}"/>
        </@head>
        <#elseif type == "commentsAnonymous">
        <@head title="${anonymousCommentLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${anonymousCommentLabel}"/>
        </@head>
        <#elseif type == "linkForge">
        <@head title="${linkForgeLabel} - ${user.userNickname} - ${symphonyLabel}">
        <meta name="description" content="${user.userNickname}${deLabel}${linkForgeLabel}"/>
        </@head>
        </#if>
        <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}" />
        <link rel="stylesheet" href="${staticServePath}/js/lib/highlight.js-9.6.0/styles/github.css">
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="content contentUserHome" id="home-pjax-container">
                    <#if pjax><!---- pjax {#home-pjax-container} start ----></#if><div<#if type != "linkForge"> class="module"</#if>>
                    <#nested>
                    </div><#if pjax><!---- pjax {#home-pjax-container} end ----></#if>
                </div>
                <div class="side contentUserHome">
                    <#include "home-side.ftl">
                    <div class="module fn-none">
                        <div class="module-header"><h2>${goHomeLabel}</h2></div>
                        <div class="module-panel fn-oh">
                            <nav class="home-menu">
                                <a pjax-title="${articleLabel} - ${user.userNickname} - ${symphonyLabel}" <#if type == "home" || type == "comments" || type == "articlesAnonymous" || type == "commentsAnonymous">
                                    class="current"</#if>
                                    href="${servePath}/member/${user.userName}"><svg height="18" viewBox="0 1 16 16" width="16">${boolIcon}</svg> ${postLabel}</a>
                                <a pjax-title="${watchingArticlesLabel} - ${user.userNickname} - ${symphonyLabel}" <#if type == "watchingArticles" || type == "followingUsers" || type == "followingTags" || type == "followingArticles" || type == "followers"> class="current"</#if>
                                    href="${servePath}/member/${user.userName}/watching/articles"><svg height="18" viewBox="0 1 14 16" width="14">${starIcon}</svg> ${followLabel}</a>
                                
                                <#if isLoggedIn && (currentUser.userName == user.userName)>
                                <a pjax-title="${pointLabel} - ${user.userName} - ${symphonyLabel}" <#if type == "points"> class="current"</#if> href="${servePath}/member/${user.userName}/points">
                                    <svg height="18" viewBox="0 1 14 16" width="14">${giftIcon}</svg> ${pointLabel}</a>
                                 </#if>   
                               <#-- <a pjax-title="${linkForgeLabel} - ${user.userName} - ${symphonyLabel}" <#if type == "linkForge"> class="current"</#if> href="${servePath}/member/${user.userName}/forge/link">
                                    <svg height="18" viewBox="0 1 16 16" width="16">${baguaIcon}</svg>  ${forgeLabel}</a> -->
                            </nav>
                        </div>
                    </div>
                </div>
        	</div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/settings${miniPostfix}.js?${staticResourceVersion}"></script>
        <script src="${staticServePath}/js/begeek${miniPostfix}.js?${staticResourceVersion}"></script>
        <script>
            Label.followLabel = "${followLabel}";
            Label.unfollowLabel = "${unfollowLabel}";
            Label.subscribeToLabel = "${subscribeToLabel}";
            Label.unsubscribeToLabel = "${unsubscribeToLabel}";
            Label.invalidPasswordLabel = "${invalidPasswordLabel}";
            Label.amountNotEmpty = "${amountNotEmpty}";
            Label.invalidUserNameLabel = "${invalidUserNameLabel}";
            Label.loginNameErrorLabel = "${loginNameErrorLabel}";
            Label.updateSuccLabel = "${updateSuccLabel}";
            Label.transferSuccLabel = "${transferSuccLabel}";
            Label.invalidUserURLLabel = "${invalidUserURLLabel}";
            Label.tagsErrorLabel = "${tagsErrorLabel}";
            Label.invalidUserQQLabel = "${invalidUserQQLabel}";
            Label.invalidUserIntroLabel = "${invalidUserIntroLabel}";
            Label.invalidUserB3KeyLabel = "${invalidUserB3KeyLabel}";
            Label.invalidUserB3ClientURLLabel = "${invalidUserB3ClientURLLabel}";
            Label.confirmPwdErrorLabel = "${confirmPwdErrorLabel}";
            Label.invalidUserNicknameLabel = "${invalidUserNicknameLabel}";
            Label.forgeUploadSuccLabel = "${forgeUploadSuccLabel}";
            Label.type = '${type}';
            Label.userName = '${user.userName}';

            Settings.initHome();

        </script>
    </body>
</html>
</#macro>
