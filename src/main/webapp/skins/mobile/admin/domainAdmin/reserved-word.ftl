<#include "macro-admin.ftl">
<@admin "reservedWords">
<div class="wrapper">

    <#if permissions["rwUpdateReservedWordBasic"].permissionGrant>
    <div class="module">
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/reserved-word/${word.oId}" method="POST">
                <label for="optionValue">${reservedWordLabel}</label>
                <input type="text" id="optionValue" name="optionValue" value="${word.optionValue}" />

                <br/><br/>
                <button type="submit" class="blue fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["rwRemoveReservedWord"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2 class="ft-red">${removeLabel}敏感词</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/remove-reserved-word" method="POST" onsubmit="return window.confirm('${confirmRemoveLabel}')">
                <input type="hidden" id="id" name="id" value="${word.oId}" readonly="readonly"/>

                <br/><br/>
                <button type="submit" class="red fn-left" >${removeLabel}</button>
            </form>
        </div>
    </div>
    </#if>
</div>
</@admin>