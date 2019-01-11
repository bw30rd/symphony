<#macro subNav type curDomain>
<div class="main">
    <div id="subNavs" class="domains fn-clear fn-flex">
        <div>
            <#list domains as domain>
            <a href="${servePath}/tag/${domain.domainURI}" <#if selected?? && selected == domain.domainURI> class="selectedZq"</#if>>${domain.domainTitle}</a>
            </#list>

            <#--<a href="${servePath}/recent"<#if 'recent' == type> class="selected"</#if>>${latestLabel}</a>
            <#if isLoggedIn && "" != currentUser.userCity>
            <a href="${servePath}/city/my"<#if 'city' == type> class="selected"</#if>>${currentUser.userCity}</a>
            </#if>
            <a href="${servePath}/timeline"<#if 'timeline' == type> class="selected"</#if>>${timelineIcon}</svg> ${timelineLabel}</a>-->
        </div>
    </div>
</div>
<div id="post" style="position:fixed;right:15px;bottom:100px; z-index:999;">
    <button class="postArticleBtn" onclick="window.location.href = '${servePath}/post?type=0'"></button>
</div>
<div id="toTop" style="position:fixed;right:15px;bottom:50px; z-index:1000;">
    <a class="toTopBtn" href="#top"></a>
</div>

<script type="text/javascript">  
    function reNavs(){
        if($('#subNavs a.selectedZq').length>0){
            var w=$('#subNavs').width();
            var iw=0;
            for(var i=0;i<$('#subNavs a').length;i++){
                iw=iw+($($('#subNavs a')[i]).width()+30);
                if($($('#subNavs a')[i]).hasClass('selectedZq')){
                    break ;
                }
            }
            if(iw>w){
                $('#subNavs').scrollLeft((iw-w)+30);
            }
        }
    }
    //获取鼠标滚动事件 
    var scrollFunc = function (e) { 
        //（IE、Opera、 Safari、Firefox、Chrome）中Firefox 使用detail，其余四类使用wheelDelta
        //detail与wheelDelta只各取两个 值，detail只取±3，wheelDelta只取±120，其中正数表示为向上，负数表示向下。  
        if (e.wheelDelta > 0 || e.detail > 0) {   
            $("#post,#toTop").show();
        } else{    
            $("#post,#toTop").hide();
        }    
    }
    /*注册事件*/  
    if (document.addEventListener) {  
        document.addEventListener('DOMMouseScroll', scrollFunc, false);
    }//W3C    
    window.onmousewheel = document.onmousewheel = scrollFunc; //IE/Opera/Chrome/Safari 
</script>

<script type="text/javascript">
//全局变量，触摸开始位置
var startX = 0, startY = 0;
            
//touchstart事件
function touchSatrtFunc(evt) {
    try {
        //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等

        var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标
        //记录触点初始位置
        startX = x;
        startY = y;

    }catch (e) {
        alert('touchSatrtFunc：' + e.message);
    }
}

//touchmove事件，这个事件无法获取坐标
function touchMoveFunc(evt) {
    try{
        //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等
        var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标

        //判断滑动方向
        if (y - startY > 0) {
        	$("#post,#toTop").show();
        }else{
        	$("#post,#toTop").hide();
        }
    }
    catch (e) {
        alert('touchMoveFunc：' + e.message);
    }
}

//touchend事件
function touchEndFunc(evt) {
    try {
        //evt.preventDefault(); //阻止触摸时浏览器的缩放、滚动条滚动等
    }
    catch (e) {
        alert('touchEndFunc：' + e.message);
    }
}

//绑定事件
function bindEvent() {
    document.addEventListener('touchstart', touchSatrtFunc, false);
    document.addEventListener('touchmove', touchMoveFunc, false);
    document.addEventListener('touchend', touchEndFunc, false);
}

//判断是否支持触摸事件
function isTouchDevice() {
    try {
        bindEvent(); //绑定事件
    }
    catch (e) {
        alert("不支持TouchEvent事件！" + e.message);
    }
}

window.onload = isTouchDevice;


setTimeout(function () {
    reNavs()
}, 500)
</script>
</#macro>