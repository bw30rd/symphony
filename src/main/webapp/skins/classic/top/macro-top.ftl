<#macro top type>
<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <#if type == "balance">
        <@head title="${wealthRankLabel} - ${symphonyLabel}">
        <link rel="canonical" href="${servePath}/top/balance">
        </@head>
        </#if>
        <#if type == "consumption">
        <@head title="${consumptionRankLabel} - ${symphonyLabel}">
        <link rel="canonical" href="${servePath}/top/consumption">
        </@head>
        </#if>
        <#if type == "checkin">
        <@head title="${checkinTopLabel} - ${symphonyLabel}">
        <link rel="canonical" href="${servePath}/top/checkin">
        </@head>
        </#if>
        <#if type == "usersRank">
        <@head title="等级排行 - ${symphonyLabel}">
        <link rel="canonical" href="${servePath}/top/usersRank">
        </@head>
        </#if>
        <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
        <link rel="stylesheet" href="${staticServePath}/css/responsive.css?${staticResourceVersion}" />
    </head>
    <body class="index">
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="content" >
                    <div class="module person-info">
                    <#nested>
                       <#-- <div class="top-ranking">
                            <#include "../common/ranking.ftl">
                        </div> -->
                    </div>
                </div>
                <#if isLoggedIn>
                <div class="index-side">
               	 	<#include "../common/begeekUser-info.ftl">
                </div>
                </#if>
            </div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/settings${miniPostfix}.js?${staticResourceVersion}"></script>
    </body>
</html>
<script>
$('.progressIn_zq').width(((parseInt($('#upCount').html())/parseInt($('#downCount').html())).toFixed(2)*100)+'%');
</script>
</#macro>
