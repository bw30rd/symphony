<#include "macro-admin.ftl">
<@admin "articles">
<div class="content">
    <#if permissions["articleUpdateArticleBasic"].permissionGrant>
    <div class="module">
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/article/${article.oId}" method="POST">
                <label>${perfectLabel}</label>
                <select id="articlePerfect" name="articlePerfect">
                    <option value="0"<#if 0 == article.articlePerfect> selected</#if>>${noLabel}</option>
                    <option value="1"<#if 1 == article.articlePerfect> selected</#if>>${yesLabel}</option>
                </select>

                <label>${commentableLabel}</label>
                <select id="articleCommentable" name="articleCommentable">
                    <option value="true"<#if article.articleCommentable> selected</#if>>${yesLabel}</option>
                    <option value="false"<#if !article.articleCommentable> selected</#if>>${noLabel}</option>
                </select>

                <label>${articleStatusLabel}</label>
                <select id="articleStatus" name="articleStatus">
                    <option value="0"<#if 0 == article.articleStatus> selected</#if>>${validLabel}</option>
                    <option value="1"<#if 1 == article.articleStatus> selected</#if>>${banLabel}</option>
                </select>
                
                <br/><br/>
                <button type="submit" class="blue fn-right" >${saveLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["articleStickArticle"].permissionGrant && permissions["articleCancelStickArticle"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${stickLabel}帖子</h2>
        </div>
        <div class="module-panel form fn-clear">
        	<#if article.articleStick == 0> 
            <form action="${servePath}/domainAdmin/${domain.domainURI}/stick-article" method="POST">
                <input type="hidden" id="articleId" name="articleId" value="${article.oId}" readonly="readonly"/>

                <button type="submit" class="blue fn-left" >置顶</button>
            </form>
            <#else>
            <form action="${servePath}/domainAdmin/${domain.domainURI}/cancel-stick-article" method="POST">
                <input type="hidden" id="articleId" name="articleId" value="${article.oId}" readonly="readonly"/>

                <button type="submit" class="gray fn-left" >取消置顶</button>
            </form>
            </#if>
        </div>
    </div>
    </#if>

    <#if permissions["articleRemoveArticle"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2 class="ft-red">${removeArticleLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/domainAdmin/${domain.domainURI}/remove-article" method="POST" onsubmit="return window.confirm('${confirmRemoveLabel}')">
               <input type="hidden" id="articleId" name="articleId" value="${article.oId}" readonly="readonly"/>

                <button type="submit" class="red fn-left" >${removeLabel}</button>
            </form>
        </div>
    </div>
    </#if>
</div>
</@admin>