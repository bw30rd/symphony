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
        <@head title="${commentAdminLabel} - ${symphonyLabel}"></@head>
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
    </head>
    <body>
        <#include "../../header.ftl">
        <div class="main">
            <div class="tab-current fn-clear">
                <div class="fn-hr5"></div>
                <div onclick="$(this).next().next().slideToggle()">
                    <#if type == "index">
                    ${consoleIndexLabel}
                    </#if>
                    <#if (type == "articles" || type == "addArticle") >
                    ${articleAdminLabel}
                    </#if>
                    <#if type == "comments" >
                    ${commentAdminLabel}
                    </#if>
                    <#if (type == "reservedWords" || type == "addReservedWord")>
                    ${reservedWordAdminLabel}
                    </#if>
                    <span class="icon-chevron-down fn-right"></span>
                </div>
                <div class="fn-hr5"></div>
                <ul class="tab fn-clear fn-none">
                    <li<#if type == "index"> class="fn-none"</#if>><a href="${servePath}/domainAdmin/${domain.domainURI}">${consoleIndexLabel}</a></li>
                    <li<#if type == "articles" || type == "addArticle"> class="fn-none"</#if>><a href="${servePath}/domainAdmin/${domain.domainURI}/articles">${articleAdminLabel}</a></li>
                    <li<#if type == "comments"> class="fn-none"</#if>><a href="${servePath}/domainAdmin/${domain.domainURI}/comments">${commentAdminLabel}</a></li>
                    <li<#if type == "reservedWords" || type == "addReservedWord"> class="fn-none"</#if>><a href="${servePath}/domainAdmin/${domain.domainURI}/reserved-words">${reservedWordAdminLabel}</a></li>
                </ul>
            </div>
            <div class="fn-hr10"></div>
            <#nested>
        </div>
        <#include "../../footer.ftl">
    </body>
</html>
</#macro>
