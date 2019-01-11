<#macro pagination url query>
<#if paginationPageCount?? && paginationPageCount!=0 && paginationPageCount!=1>
<div class="fn-clear">
    <div class="pagination">
        <#if paginationCurrentPageNum!=1>
        <a rel="prev" onclick="window.location='${url}?p=${paginationCurrentPageNum-1}&${query}'" style="height:17px;"><</a>
        </#if>

        <select data-url="${url}" <#if query != ''>data-param="${query}"</#if>>
            <#list 1..paginationLastPageNum as nums>
            <option <#if nums == paginationCurrentPageNum> selected="selected"</#if>>${nums}</option>
            </#list>
        </select>

        <#if paginationLastPageNum gt paginationCurrentPageNum>
        <a rel="next" onclick="window.location='${url}?p=${paginationCurrentPageNum+1}&${query}'" style="height:17px;">></a>
        </#if>
    </div>
</div>
<#else>
<div class="fn-hr10"></div>
</#if>
</#macro>