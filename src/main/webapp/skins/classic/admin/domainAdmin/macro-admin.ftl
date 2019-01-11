<#macro admin type>
<#include "../../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <#if type == "index">
        <@head title="${consoleIndexLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "users">
        <@head title="${userAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "addUser">
        <@head title="${addUserLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "articles">
        <@head title="${articleAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "comments">
        <@head title="${commentAdminLabel} - ${symphonyLabel}">
        <link rel="stylesheet" href="${staticServePath}/js/lib/highlight.js-9.6.0/styles/github.css">
        </@head>
        </#if>
        <#if type == "addDomain">
        <@head title="${addDomainLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "domains">
        <@head title="${domainAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "tags">
        <@head title="${tagAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "addTag">
        <@head title="${addTagLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "reservedWords">
        <@head title="${reservedWordAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "addReservedWord">
        <@head title="${allReservedWordLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "addArticle">
        <@head title="${addArticleLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "invitecodes">
        <@head title="${invitecodeAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "ad">
        <@head title="${adAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "misc">
        <@head title="${miscAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "roles">
            <@head title="${rolesAdminLabel} - ${symphonyLabel}"></@head>
        </#if>
        <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}" />
    </head>
    <body>
        <#include "../../header.ftl">
        <div class="main">
            <div class="wrapper">
                <#nested>
                <div class="side">
                    <div class="module">
                        <div class="module-panel fn-oh">
                            <nav class="home-menu">
                                <a href="${servePath}/domainAdmin/${domain.domainURI}"<#if type == "index"> class="current"</#if>>${consoleIndexLabel}</a>
                                <a href="${servePath}/domainAdmin/${tag.tagURI}/articles"<#if type == "articles" || type == "addArticle"> class="current"</#if>>${articleAdminLabel}</a>
                                <a href="${servePath}/domainAdmin/${tag.tagURI}/comments"<#if type == "comments"> class="current"</#if>>${commentAdminLabel}</a>
                                <a href="${servePath}/domainAdmin/${tag.tagURI}/reserved-words"<#if type == "reservedWords" || type == "addReservedWord"> class="current"</#if>>${reservedWordAdminLabel}</a>
                           	</nav>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <#include "../../footer.ftl">
        <#if type == "comments">
        <script src="${staticServePath}/js/settings${miniPostfix}.js?${staticResourceVersion}"></script>
        <script>
            Settings.initHljs();
        </script>
        </#if>
    </body>
</html>
</#macro>
