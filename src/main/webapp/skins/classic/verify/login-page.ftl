<!DOCTYPE html>
<html>
<body>
	<div style="width: 250px; height: 250px; margin-left: 70px">
		<img src="${staticServePath}/images/loading.gif" height="100%"
			width="100%">
	</div>
</body>
<script
	src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}"></script>

<script>
	setTimeout(function() {
		window.location.href = 'https://authcenter.bwae.org/authorcenter/wechatlogin/messageredirect?label=begeek&redirecturl=http://172.16.3.83:8080/symphony/begeek/login?from=wechat';
	}, 1000);
</script>

</html>