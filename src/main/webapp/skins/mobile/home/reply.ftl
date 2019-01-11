<div id="reply" class="form fn-clear comment-wrap" class="replyBox" style="display: none;">
    <!-- <div id="replyUseName"> </div> -->
    <!-- <textarea id="commentContent" placeholder="${commentEditorPlaceholderLabel}"></textarea> -->
    <textarea id="commentContent" style="height:168px;"></textarea>
    <div class="tip" id="addCommentTip"></div>
    <div class="fn-clear comment-submit">
        <#if (commonAddCommentAnonymous ?? &&commonAddCommentAnonymous == 1) || permissions["commonAddCommentAnonymous"].permissionGrant>
        <label class="anonymous-check" style="display:block; float:none; margin-bottom:10px;">${anonymousLabel}<input type="checkbox" id="commentAnonymous"></label>
        </#if>
        <button class="blue fn-left submitGreenBtn" onclick="Comment.add('${article.oId}', '${csrfToken}');reply_hide()">${submitLabel}</button>
        <button class="blue fn-right backWhiteBtn" onclick="reply_hide()">返回</button>
    </div>
</div>