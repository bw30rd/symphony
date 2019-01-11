<#include "macro-settings.ftl">
<@home "i18n">
<div class="module">
    <div class="module-header">
        <h2>${i18nTipLabel}</h2>
    </div>

    <div class="module-panel form fn-clear">
        <br/>
        <label>${languageLabel}</label>
        <select id="userLanguage">
            <#list languages as language>
            <option value="${language}" <#if language == user.userLanguage>selected</#if>>${language}</option>
            </#list>
        </select>
        <br/><br/>
        <label>${timezoneLabel}</label>
        <select id="userTimezone">
            <#list timezones as timezone>
            <option value="${timezone.id}" <#if timezone.id == user.userTimezone>selected</#if>>${timezone.name}</option>
            </#list>
        </select>

        <br/><br/>
        <div class="fn-clear"></div>
        <div id="i18nTip" class="tip"></div><br/>
        <button class="blue fn-left submitGreenBtn" onclick="Settings.update('i18n', '${csrfToken}')">${saveLabel}</button>
        <button class="blue fn-right backWhiteBtn" onclick="javascript:history.back(-1);">返回</button>
    </div>
</div>
</@home>