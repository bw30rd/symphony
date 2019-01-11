<#include "../macro-head.ftl">
<!DOCTYPE html>
<html style="height:100%;">
    <head>
        <@head title="${loginLabel} - ${symphonyLabel}">
        <meta name="description" content="${registerLabel} ${symphonyLabel}"/>
        </@head>
        <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
        <link rel="canonical" href="${servePath}/register">
        <style>
            .ifr {
                width: 100%;
                height: 280px;
                border: none;
                display: block;
                margin-top: 20px;
                margin-left: 0px;
            }
            .verify .verify-wrap {
                width: 100%;
            }
            .verify .intro {
                width: 100%;
                padding: 10px;
            }
            h1 {
                text-align: center;
            }
        </style>
    </head>
    <body class="mobileLoginBg" style="box-sizing: border-box;">
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="verify-wrap">
                
                   <#-- <div class="form" style="display:none;">
                        ${logoIcon2}
                        <div class="input-wrap">
                            <span class="icon-userrole"></span>
                            <input id="nameOrEmail" type="text" autofocus="autofocus" placeholder="${nameOrEmailLabel}" autocomplete="off" />
                        </div>
                        <div class="input-wrap">
                            <span class="icon-locked"></span>
                            <input type="password" id="loginPassword" placeholder="${passwordLabel}" />
                        </div>
                        <div class="fn-none input-wrap">
                            <img id="captchaImg" class="captcha-img fn-pointer" />
                            <input type="text" id="captchaLogin" class="captcha-input" placeholder="${captchaLabel}" />
                        </div>

                        <div class="fn-clear">
                            <div class="fn-hr5"></div>
                            <input type="checkbox" id="rememberLogin" checked /> ${rememberLoginStatusLabel}
                            <a href="${servePath}/forget-pwd" class="fn-right">${forgetPwdLabel}</a>
                            <div class="fn-hr5"></div>
                        </div>
                        
                        <div id="loginTip" class="tip"></div>
                        <button class="green" onclick="Verify.login('${goto}')">${loginLabel}</button>
                        <button onclick="Util.goRegister()">${registerLabel}</button>
                    </div>-->


					<a class="begeekLogoMobile"></a>
                    <iframe src="/symphony/begeek" name="login_box" class="ifr"></iframe>
                </div>
                <#--<div class="intro content-reset">
                    ${introLabel}
                </div>-->
            </div>
        </div>
        <script src="${staticServePath}/js/lib/compress/libs.min.js?${staticResourceVersion}"></script>
        <script src="${staticServePath}/js/common.js?${staticResourceVersion}"></script>
        <script>
            var Label = {
                invalidPasswordLabel: "${invalidPasswordLabel}",
                loginNameErrorLabel: "${loginNameErrorLabel}",
                followLabel: "${followLabel}",
                unfollowLabel: "${unfollowLabel}",
                symphonyLabel: "${symphonyLabel}",
                visionLabel: "${visionLabel}",
                cmtLabel: "${cmtLabel}",
                collectLabel: "${collectLabel}",
                uncollectLabel: "${uncollectLabel}",
                desktopNotificationTemplateLabel: "${desktopNotificationTemplateLabel}",
                servePath: "${servePath}",
                staticServePath: "${staticServePath}",
                isLoggedIn: ${isLoggedIn?c},
                funNeedLoginLabel: '${funNeedLoginLabel}',
                notificationCommentedLabel: '${notificationCommentedLabel}',
                notificationReplyLabel: '${notificationReplyLabel}',
                notificationAtLabel: '${notificationAtLabel}',
                notificationFollowingLabel: '${notificationFollowingLabel}',
                pointLabel: '${pointLabel}',
                sameCityLabel: '${sameCityLabel}',
                systemLabel: '${systemLabel}',
                newFollowerLabel: '${newFollowerLabel}',
                makeAsReadLabel: '${makeAsReadLabel}',
                checkIcon: '${checkIcon}'<#if isLoggedIn>,
                currentUserName: '${currentUser.userName}'</#if>
            };
            Util.init(${isLoggedIn?c});
            
            <#if isLoggedIn>
            // Init [User] channel
            Util.initUserChannel("${wsScheme}://${serverHost}:${serverPort}${contextPath}/user-channel");
            </#if>
        </script>
        <#if algoliaEnabled>
        <script src="${staticServePath}/js/lib/algolia/algolia.min.js"></script>
        <script>
            Util.initSearch('${algoliaAppId}', '${algoliaSearchKey}', '${algoliaIndex}');
        </script>
        </#if>
        <script src="${staticServePath}/js/verify${miniPostfix}.js?${staticResourceVersion}"></script>
        <script>
            Verify.init();
            Label.userNameErrorLabel = "${userNameErrorLabel}";
            Label.invalidEmailLabel = "${invalidEmailLabel}";
            Label.confirmPwdErrorLabel = "${confirmPwdErrorLabel}";
            Label.captchaErrorLabel = "${captchaErrorLabel}";
        </script>
    </body>
    <script>
        function findSession(){
            if(window.localStorage.loginX == 1){
                window.localStorage.loginX = 0 ;
                window.location.reload(); 
            }else{
                setTimeout(function(){
                    findSession();
                },1000);
            }
        }
        findSession();
    </script>
</html>

