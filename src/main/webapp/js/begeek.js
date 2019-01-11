
var Begeek = {
	getUserRank:function(){	
		if($("#userRankList .option").css('display') == 'none'){
			$("#userRankList").toggle();
			$("#userInfo").toggle();
		} else {
			$("#userRankList").show();
			$("#userInfo").hide();
		}
		$("#userRankList .option").hide();
		$("#userRank").find("li").remove(); 
		$("#userRank").find("a").remove(); 
		
		$.ajax({
			url: Label.servePath + "/top/userRank",
			type: "GET",
			cache: false,
			dataType: "json",
			success: function (result, textStatus) {
				users = result.users;
				for(var i = 0; i < users.length; i++){
					$("#userRank").append(
						"<li>"+
						'<div><a class="rightRank rightRank'+i+'">'+(i+1)+'</a></div>'+
						"<div><a href='"+Label.servePath+"/member/"+users[i].userName+"'><div class='avatar' style='background-image:url("+users[i].userAvatarURL+")'></div></a><a href='"+Label.servePath+"/member/"+users[i].userName+"'> "+users[i].userNickname+"</a></div>"+
						"<div><a class='"+users[i].userLevelType+"'>"+users[i].userLevel+"</a></div>"+
						"<div>"+users[i].userPoint+"</div>"+
						"</li>"
					);
				}
				$("#userRank").append("<li class='lastMore'><a href='"+Label.servePath+"/top/usersRank'>查看更多</a></li>");
			}
		});
	},
	fetchLiveness : function(type) {
		if ($("#userRankList .option").css('display') == 'none') {
			$("#userRankList").show();
			$("#userInfo").hide();
		} else {
			$("#userRankList").toggle();
			$("#userInfo").toggle();
		}
				
    	$("#userRankList .option").show();
    	$("#userRank").find("li").remove();
    	$("#userRank").find("a").remove();
    	
    	$.ajax({
            url: Label.servePath + "/top/userLiveness",
            type: "GET",
            cache: false,
            data:{livenessType:type},
            dataType: "json",
            success: function (result, textStatus) {
            	users = result.users;
            	for(var i = 0; i<users.length; i++){
					$("#userRank").append(
						"<li>"+
						'<div><a class="rightRank rightRank'+i+'">'+(i+1)+'</a></div>'+
						"<div><a href='"+Label.servePath+"/member/"+users[i].userName+"'><div class='avatar' style='background-image:url("+users[i].userAvatarURL+")'></div></a><a href='"+Label.servePath+"/member/"+users[i].userName+"'>  "+users[i].userNickname+"</a></div>"+
						"<div><a class='"+users[i].userLevelType+"'>"+users[i].userLevel+"</a></div>"+
						"<div>"+users[i].livenessPoint+"</div>"+
						"</li>"
					);
            	}
            }
        });
	},
	getUserLiveness : function() {
		if ($("#userRankList .option").css('display') == 'none') {
			var target = $("#userRankList .selected");
			if (target == null || 0 == target.length) {
				target = $("#userRankList .week");
			}
			if ($(target).hasClass("week")) {
				Begeek.getWeekLiveness(target);
			} else {
				Begeek.getMoonLiveness(target);
			}
		} else {
			$("#userRankList").hide();
			$("#userRankList .option").hide();
			$("#userInfo").show();
		}

	},
	getWeekLiveness : function(target) {
		$(target).parent().find('a').removeClass('selected');
		$(target).addClass('selected');
		$("#userRankList .option").hide();
		Begeek.fetchLiveness(0);
	},
	getMoonLiveness : function(target) {
		$(target).parent().find('a').removeClass('selected');
		$(target).addClass('selected');
		$("#userRankList .option").hide();
		Begeek.fetchLiveness(1);
	}
};
