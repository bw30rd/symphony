<#macro search type>
<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <#if type == "article">
        <@head title="帖子搜索 - ${symphonyLabel}"></@head>
        </#if>
        <#if type == "users">
        <@head title="用户搜索 - ${symphonyLabel}"></@head>
        </#if>
        <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
        <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}" />
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <#nested>
                
                <#if isLoggedIn>
                <div class="side">
                    <#include "../side.ftl">
                </div>
                </#if>
             </div>   
        </div>
        <#include "../footer.ftl">
    </body>
</html>
</#macro>