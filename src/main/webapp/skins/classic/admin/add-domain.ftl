<#include "macro-admin.ftl">
<@admin "addDomain">
<div class="content">
    <#if permissions["domainAddDomain"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${addDomainLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/add-domain" method="POST" accept-charset="utf-8" onsubmit="document.charset='utf-8';">
                <label>${titleLabel}</label>
                <input name="domainTitle" type="text" />

                <br/><br/><br/>
                <button type="submit" class="blue fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>
</div>
</@admin>