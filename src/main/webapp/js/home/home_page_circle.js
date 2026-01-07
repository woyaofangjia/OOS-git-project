/**
 * 简化的轮播脚本，适应新的HTML结构
 */
$(function () {
    var time_out = setTimeout(moveToRight, 3500);
    var isMove = false;
    
    // 点击右箭头
    $('.right_turn').click(function () {
        if (!isMove) {
            clearTimeout(time_out);
            isMove = true;
            moveToRight();
        }
    });
    
    // 点击左箭头
    $('.left_turn').click(function () {
        if (!isMove) {
            clearTimeout(time_out);
            isMove = true;
            moveToLeft();
        }
    });
    
    // 移动到下一个轮播项
    function moveToRight() {
        var current = $('.my_slide.current');
        var next = current.next('.my_slide');
        
        if (next.length === 0) {
            next = $('.my_slide:first');
        }
        
        // 淡出当前项，淡入下一项
        current.css('z-index', 10).animate({opacity: 0}, 500, function() {
            current.removeClass('current');
            
            next.css('z-index', 20).animate({opacity: 1}, 500, function() {
                next.addClass('current');
                
                // 重置当前项的z-index和opacity，为下次显示做准备
                current.css({'z-index': 0, 'opacity': 0});
                
                time_out = setTimeout(moveToRight, 3500);
                isMove = false;
            });
        });
    }
    
    // 移动到上一个轮播项
    function moveToLeft() {
        var current = $('.my_slide.current');
        var prev = current.prev('.my_slide');
        
        if (prev.length === 0) {
            prev = $('.my_slide:last');
        }
        
        // 淡出当前项，淡入上一项
        current.css('z-index', 10).animate({opacity: 0}, 500, function() {
            current.removeClass('current');
            
            prev.css('z-index', 20).animate({opacity: 1}, 500, function() {
                prev.addClass('current');
                
                // 重置当前项的z-index和opacity，为下次显示做准备
                current.css({'z-index': 0, 'opacity': 0});
                
                time_out = setTimeout(moveToRight, 3500);
                isMove = false;
            });
        });
    }
    $('.buy').click(function () {
        var id = $(this).attr('value');
        $.ajax({
            url:'/insertGoodsCar.do',
            dataType:'JSON',
            type:'post',
            data:{id:id},
            success:function (data) {
                var result = data.result;
                if (result == '2'){
                    alert('您还未登录，请先登录！！！');
                } else if (result == '1'){
                    alert('加入购物车成功');
                } else if (result == '0'){
                    alert('加入购物车失败');
                } else {
                    alert('发生了错误，请检测网络');
                }
            }
        })
    });

    // var host = window.location.host;
    // var websocket = new WebSocket("ws://" + host + "/sockjs/webSocketIMServer");
    // var phone = $('#user_name_a').attr('value');
    // if (phone !== 'wsk') {
    //     websocket.onopen = function () {
    //         console.log("websocket连接上");
    //         websocket.send("start");
    //     };
    //     websocket.onmessage = function (evnt) {
    //         // console.log(evnt.data);
    //         var result = evnt.data;
    //         if (result == "error"){
    //             window.location.href='/logout.do';
    //             alert("该账号在其他地方登录了，请检查是否为本人操作，防止密码丢失！！！");
    //             return;
    //         }
    //         setTimeout(function () {
    //             messageHandle();
    //         }, 2000);
    //     };
    //     websocket.onerror = function () {
    //         console.log("websocket错误");
    //     };
    //     websocket.onclose = function () {
    //         console.log("websocket关闭");
    //     };
    //     function messageHandle() {
    //         // alert(phone);
    //         websocket.send(phone);
    //     };
    // }
});
