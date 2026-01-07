/**
 * Created by alone on 2017/5/13.
 */
$(function () {
    // 确保页面加载时personal_nav初始状态为隐藏
    $('.personal_nav').hide();
    
    $('body').click(function (e) {
        if (e.clientX > 150 || e.clientY > 300) {
            if ($('.short_nav_show').is(":visible")) {
                $('.short_nav_show').animate({
                    opacity: 0,
                    height: 0
                }, 500, function () {
                    $(this).hide(0);
                });
            }
            if ($('.personal_nav').is(":visible")) {
                $('.personal_nav').animate({height: '0%'}, 300).hide(0);
            }
        }
    });
    // 修改滚动事件处理，使搜索栏和导航栏始终同时显示
    $(window).scroll(function () {
        // 始终保持搜索栏和导航栏可见
        $('.my_nav').css({opacity: 1}); // 导航栏始终完全可见
        $('.short_nav').css({opacity: 1}); // 搜索栏始终完全可见
    });
    
    // 页面加载时立即设置两者都可见
    $(document).ready(function() {
        $('.my_nav').css({opacity: 1});
        $('.short_nav').css({opacity: 1});
    });

    $('.nav_search_input').bind("focus", function () {
        $(this).animate({width: "15%", marginLeft: "20%"}, 800);
    });

    $('.nav_search_input').bind("blur", function () {
        if ($(this).val() == '')
            $(this).animate({width: "5em", marginLeft: "25%"}, 800);
    });

    $('.search_icon').click(function () {

    });
    $('.short_nav').click(function () {
        if ($('.short_nav').css('opacity') > 0.5) {
            if ($('.short_nav_show').is(":visible")) {
                $('.short_nav_show').animate({
                    opacity: 0,
                    height: 0
                }, 500, function () {
                    $(this).hide(0);
                });
            } else {
                $('.short_nav_show').show(0).css({opacity: 0, height: 0}).animate({
                    opacity: 1,
                    height: "30%"
                }, 500).show(0);
            }
        }
    });
    // 为用户名和用户图标都添加鼠标悬停事件，确保个人导航栏能正确显示
    // 确保事件能正确绑定，即使元素是动态加载的
    $(document).on('mouseenter', '.user_name_a, .user_icon', function () {
        if (!$('.personal_nav').is(":visible")) {
            // 确保personal_nav有正确的z-index，能显示在其他内容之上
            $('.personal_nav').css('z-index', '1000').show(0).animate({height: '41%'}, 500);
        }
    });
    // 修改mouseleave事件处理，增加延迟并确保点击事件优先处理
    var leaveTimer;
    $('.personal_nav').mouseleave(function () {
        if ($('.personal_nav').is(":visible")) {
            leaveTimer = setTimeout(function() {
                $('.personal_nav').animate({height: '0%'}, 300, function() {
                    $(this).hide(0).css({height: 'auto'}); // 重置高度为auto，以便下次显示正确
                });
            }, 200); // 增加200ms延迟，给用户更多时间点击底部菜单项
        }
    });
    
    // 为导航菜单项添加点击事件，确保点击优先于mouseleave
    $('.personal_nav a').click(function(e) {
        clearTimeout(leaveTimer); // 清除延迟计时器，确保链接正常跳转
        // 让链接正常跳转，不做任何阻止
    });
    $('.search_icon').click(function () {
        var name = $('.nav_search_input').val();
        window.location.href = '/findShopByName.do?name=' + name;
    });
});
jQuery(document).ready(function ($) {
    // 已完全移除WebSocket连接代码，避免连接错误
    console.log('页面初始化完成');
});