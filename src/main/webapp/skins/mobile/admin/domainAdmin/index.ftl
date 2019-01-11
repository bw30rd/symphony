<#include "macro-admin.ftl">
<@admin "index">
<div class="wrapper">
    <div class="fn-hr10"></div>
    <ul>
        <li>【${domain.domainTitle}】 领域管理</li>
            <li>订阅数  ${tag.tagFollowerCount?c}</li>
            <li>帖子数  ${tag.tagReferenceCount?c}</li>
            <li>回帖数  ${tag.tagCommentCount?c}</li>
    </ul>
</div>
</@admin>
<script>
    document.addEventListener("DOMContentLoaded", function (event) {
        $.ajax({
            url: "https://rhythm.b3log.org/version/symphony/latest",
            type: "GET",
            dataType: "jsonp",
            jsonp: "callback",
            success: function (data, textStatus) {
                if ($("#version").text() === data.symphonyVersion) {
                    $("#upgrade").text('${upToDateLabel}');
                } else {
                    $("#upgrade").html('${newVersionAvailableLabel}' + '${colonLabel}'
                            + "<a href='" + data.symphonyDownload
                            + "' target='_blank'>" + data.symphonyVersion + "</a>");
                }
            }
        });
    });

</script>
