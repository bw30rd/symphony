<#include "macro-admin.ftl">
<#include "../../macro-pagination.ftl">
<@admin "articles">
<div class="content admin">
    <div class="module list">
        <ul>
            <#list articles as item>
            <li>
                <div class="fn-flex">
                    <div class="avatar tooltipped tooltipped-s" aria-label="${item.articleAuthorName}"
                         style="background-image:url('${item.articleAuthorThumbnailURL20}')"></div>
                    <div class="fn-flex-1">
                        <h2>
                            <a href="${servePath}${item.articlePermalink}">${item.articleTitle}</a>
                            <span class="ft-smaller">
                            <#if item.articleStatus == 0>
                                <span class="ft-gray">${validLabel}</span>
                                <#else>
                                <font class="ft-red">${banLabel}</font>
                            </#if>
                            <#if 0 < item.articleStick>
                            <#if 9223372036854775807 <= item.articleStick></#if><font class="ft-blue">${stickLabel}</font>
                            </#if>
                            </span>
                        </h2>
                        <span class="ft-fade ft-smaller">
                        ${item.articleTags}  • ${item.articleCreateTime?string('yyyy-MM-dd HH:mm')} •
                        ${viewCountLabel} ${item.articleViewCount} •
                        ${commentCountLabel} ${item.articleCommentCount}
                        </span>
                    </div>
                    <a href="${servePath}/domainAdmin/${domain.domainURI}/article/${item.oId}" class="fn-right tooltipped tooltipped-w ft-a-title" aria-label="${editLabel}"><span class="icon-edit"></span></a>
                </div>
            </li>
            </#list>
        </ul>
        <@pagination url="${servePath}/domainAdmin/${domain.domainURI}/articles"/>
    </div>
</div>
<script>
    function searchIndex() {
        $.ajax({
            url: "${servePath}/domainAdmin/${domain.domainURI}/search/index",
            type: "POST",
            cache: false,
            success: function (result, textStatus) {
                window.location.reload();
            }
        });
    }
</script>
</@admin>