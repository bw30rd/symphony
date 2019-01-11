<#include "macro-admin.ftl">
<@admin "comments">
<div class="content">
    <#if permissions["commentUpdateCommentBasic"].permissionGrant>
    <div class="module">
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/comment/${comment.oId}" method="POST">
                <label>${commentStatusLabel}</label>
                <select id="commentStatus" name="commentStatus">
                    <option value="0"<#if 0 == comment.commentStatus> selected</#if>>${validLabel}</option>
                    <option value="1"<#if 1 == comment.commentStatus> selected</#if>>${banLabel}</option>
                </select>

                <br/><br/>
                <button type="submit" class="blue fn-right">${saveLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["commentRemoveComment"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2 class="ft-red">${removeLabel}回帖</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/remove-comment" method="POST" onsubmit="return window.confirm('${confirmRemoveLabel}')">
                <input type="hidden" id="commentId" name="commentId" value="${comment.oId}" readonly="readonly"/>

                <button type="submit" class="red fn-left" >${removeLabel}</button>
            </form>
        </div>
    </div>
    </#if>
</div>
</@admin>