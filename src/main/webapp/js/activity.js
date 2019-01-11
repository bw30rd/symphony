/*
 * Symphony - A modern community (forum/SNS/blog) platform written in Java.
 * Copyright (C) 2012-2017,  b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @fileoverview Activity.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://zephyr.b3log.org">Zephyr</a>
 * @version 1.4.2.5, Apr 6, 2017
 */

/**
 * @description Activity
 * @static
 */
var Activity = {
    /**
     * @description 上证指数博彩活动下注
     * @argument {String} csrfToken CSRF token
     */
    bet1A0001: function (csrfToken) {
        var requestJSONObject = {
            smallOrLarge: $("input[name=smallOrLarge]:checked").val(),
            amount: $("input[name=amount]:checked").val()
        };

        $.ajax({
            url: Label.servePath + "/activity/1A0001/bet",
            type: "POST",
            headers: {"csrfToken": csrfToken},
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                if (result.sc) {
                    $("#betDiv, #betBtn").remove();
                    $("#tip").addClass("succ").removeClass('error').html('<ul><li>' + result.msg + '</li></ul>');
                } else {
                    $("#tip").addClass("error").removeClass('succ').html('<ul><li>' + result.msg + '</li></ul>');
                }

                $("#tip").show();

                setTimeout(function () {
                    $("#tip").hide();
                }, 3000);
            }
        });
    },
    /**
     * @description 上证指数博彩活动兑奖
     */
    collect1A0001: function () {
        var requestJSONObject = {
        };

        $.ajax({
            url: Label.servePath + "/activity/1A0001/collect",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tip").show();
                if (result.sc) {
                    $("#tip").addClass("succ").removeClass('error').html('<ul><li>' + result.msg + '</li></ul>');
                    $("#collectBtn").remove();
                } else {
                    $("#tip").addClass("error").removeClass('succ').html('<ul><li>' + result.msg + '</li></ul>');
                    setTimeout(function () {
                        $("#tip").hide();
                    }, 3000);
                }
            }
        });
    },
    /**
     * paint brush
     * @param {string} id canvas id.
     * @returns {undefined}
     */
    charInit: function (id) {
        var el = document.getElementById(id),
                ctx = el.getContext('2d');
        ctx.strokeStyle = '#000';
        ctx.lineWidth = 5;
        ctx.lineJoin = ctx.lineCap = 'round';
        ctx.shadowBlur = 5;
        ctx.shadowColor = 'rgb(0, 0, 0)';

        var isDrawing = false, x = 0, y = 0;

        el.onmousedown = function (e) {
            isDrawing = true;
            ctx.beginPath();
            x = e.clientX - e.target.offsetLeft + $(window).scrollLeft();
            y = e.clientY - e.target.offsetTop + $(window).scrollTop();
            ctx.moveTo(x, y);
        };

        el.onmousemove = function (e) {
            if (!isDrawing) {
                return;
            }

            x = e.clientX - e.target.offsetLeft + $(window).scrollLeft();
            y = e.clientY - e.target.offsetTop + $(window).scrollTop();
            ctx.lineTo(x, y);
            ctx.stroke();
        };

        el.onmouseup = function () {
            isDrawing = false;
        };

        el.addEventListener("touchstart", function (e) {
            ctx.beginPath();
            x = e.changedTouches[0].pageX - e.target.offsetLeft;
            y = e.changedTouches[0].pageY - e.target.offsetTop;
            ctx.moveTo(x, y);

        }, false);

        el.addEventListener("touchmove", function (e) {
            e.preventDefault();
            x = e.changedTouches[0].pageX - e.target.offsetLeft;
            y = e.changedTouches[0].pageY - e.target.offsetTop;
            ctx.lineTo(x, y);
            ctx.stroke();
        }, false);
    },
    /**
     * 提交写好字的图片.
     * 
     * @param {string} id canvas id.
     */
    submitCharacter: function (id) {
        var canvas = document.getElementById(id);

        var requestJSONObject = {
            dataURL: canvas.toDataURL(),
            character: $("h2.fn-inline a").text()
        };

        $.ajax({
            url: Label.servePath + "/activity/character/submit",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            beforeSend: function () {
                var $btn = $("button.blue");
                $btn.attr("disabled", "disabled").css("opacity", "0.3").text($btn.text() + 'ing');
            },
            success: function (result, textStatus) {
                alert(result.msg);

                if (result.sc) {
                    window.location.reload();
                }
            },
            complete: function () {
                var $btn = $("button.blue");
                $btn.removeAttr("disabled").css("opacity", "1").text($btn.text().substr(0, $btn.text().length - 3));
            }
        });
    },
    /**
     * clear canvas
     * 
     * @param {string} id canvas id.
     */
    clearCharacter: function (id) {
        var canvas = document.getElementById(id),
                ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    },
    /**
     * Eating snake
     */
    initSnake: function () {
        EatingSnake.initMap('snakeCanvas');
    },
    startSnake: function (csrfToken) {
        if (!confirm(Label.activityStartEatingSnakeTipLabel)) {
            return;
        }

        $(document).unbind("keyup");

        EatingSnake.start(csrfToken);

        document.onkeydown = function (event) {
            var event = event || window.event;
            EatingSnake.input(event.keyCode);
        };
    },
    /**
     * daily-checkin
     */
    checkin: function (csrfToken,deviceType) {
    	var  url = Label.servePath + "/activity/daily-checkin", type = "POST";
    	$.ajax({
            url: url,
            type: type,
            headers: {"csrfToken": csrfToken},
            cache: false,
            success: function (result, textStatus) {
                if (0 === result.sc) {
                	if(deviceType == "pc"){
                        // window.location.href = Label.servePath;
                        $('#signBtn').hide();
                        $('#plusCent').html('+' + (result.userPoint - result.usedPoint)).addClass('plusplus');
                        $('#uPoint,#uPoint2,#upCount').html(result.userPoint);
                        $('#downCount').html((parseInt(result.userPoint) + parseInt(result.upgradePoints)));
                        $('#uupgradePoints').html(result.upgradePoints);
                        $('#uRank').html(result.userRank);
                        $('#ucurrentUserLevel').html(result.currentUserLevel).removeClass().addClass(result.currentUserLevelType);
                        $('#signLi').show().html('已连续签到 <span style="color:#FF9933">'+ result.checkedinStreak +'</span> 天');
                        $('.progressIn_zq').width(((parseInt($('#upCount').html())/parseInt($('#downCount').html())).toFixed(2)*100)+'%');
                	}else{
                		window.location.href = Label.servePath + "/member/" + result.userName;
                	}
                }
            }
        });
    }
};

