/**
 * Created by alone on 2017/5/14.
 */
$(function () {
    // 清除旧的缓存数据，确保加载最新分类
    if (window.type_list) {
        delete window.type_list;
    }
    if (window.classificationCache) {
        delete window.classificationCache;
    }
    if (window.specificCache) {
        delete window.specificCache;
    }
    
    // 检查是否已经加载了type_list_obj.js
    if (typeof getTypeList !== 'function') {
        console.error("type_list_obj.js未正确加载，分类数据初始化失败");
    } else {
        // 获取分类数据
        var type_list = getTypeList();
    }
    
    insertShopCar();
    $(window).scroll(function () {
        var temp = $(this).scrollTop();
        if (temp > 100) {
            $('.my_type_div').css({"margin-top": "8%"});
            $('.particular_type_div').css({"margin-top": "8%"});
        } else {
            $('.my_type_div').css({"margin-top": "15%"});
            $('.particular_type_div').css({"margin-top": "15%"});
        }
    });
    // 为分类容器添加悬停事件，而不是为每个li单独添加
    var isOverParticularType = false;
    
    // 为一级分类添加鼠标进入事件
    $('.my_type_div ul li').mouseenter(function () {
        $(this).css("background-color", "#f5f5f5");
        var pid = $(this).attr("class").split('_')[1];
        addList(pid);
        $('.particular_type_div').css("display", "block");
    });
    
    // 为一级分类添加鼠标离开事件
    $('.my_type_div ul li').mouseleave(function () {
        $(this).css("background-color", "#ffffff");
        // 延迟隐藏，给用户时间移动到子分类区域
        setTimeout(function() {
            // 只有当鼠标不在子分类区域时才隐藏
            if (!isOverParticularType) {
                $('.particular_type_div').css("display", "none");
            }
        }, 200);
    });
    
    // 为子分类区域添加鼠标进入事件
    $('.particular_type_div').mouseenter(function() {
        isOverParticularType = true;
        $(this).css("display", "block");
    });
    
    // 为子分类区域添加鼠标离开事件
    $('.particular_type_div').mouseleave(function() {
        isOverParticularType = false;
        $(this).css("display", "none");
    });
function addList(pid) {
    // 清空二级分类
    $('.particular_type_div').empty();
    
    // 创建缓存对象
    if (!window.classificationCache) {
        window.classificationCache = {};
    }
    
    // 检查缓存
    if (window.classificationCache[pid]) {
        renderClassificationList(window.classificationCache[pid]);
        return;
    }
    
    // 根据pid获取分类数据，使用正确的API路径和参数名
    $.ajax({
        url: '/ajax/getClassification.do',
        type: 'POST',
        data: {pid: pid},
        dataType: 'json',
        cache: false,
        success: function(data) {
            // 缓存分类数据
            window.classificationCache[pid] = data;
            // 渲染分类列表
            renderClassificationList(data);
        },
        error: function(xhr, status, error) {
            // console.error('加载分类失败:', error);
            // console.error('请求URL:', '/ajax/getClassification.do');
            // console.error('请求参数:', {pid: pid});
        }
    });
}

// 渲染分类列表
function renderClassificationList(data) {
    // 遍历分类数据
    for (var i = 0; i < data.length; i++) {
        // 创建分类div
        var div = $('<div class="one_part"></div>');
        // 创建标题div
        var titleDiv = $('<div class="type_title_div"></div>');
        // 创建标题span
        var titleSpan = $('<span class="type_border_span">' + (i + 1) + '</span>');
        // 创建标题h3 - 使用正确的字段名
        var kindName = data[i].name || '未命名分类';
        var titleH3 = $('<h3>' + kindName + '</h3>');
        // 创建商品列表div
        var goodsListDiv = $('<div class="type_goods_list"></div>');
        
        // 处理三级分类数据 - 使用content属性，这是我们在Classification类中添加的新属性
        var specifics = data[i].content || [];
        if (specifics && specifics.length > 0) {
            // 遍历三级分类数据
            for (var j = 0; j < specifics.length; j++) {
                var specificName = specifics[j].name || '未命名子分类';
                var specificId = specifics[j].id || '';
                // 创建商品a标签
                var a = $('<a href="#" class="shop_sort" id="' + specificId + '">' + specificName + '</a>');
                // 添加商品a标签到商品列表div
                goodsListDiv.append(a);
            }
        }
        
        // 添加标题span和h3到标题div
        titleDiv.append(titleSpan);
        titleDiv.append(titleH3);
        // 添加标题div和商品列表div到分类div
        div.append(titleDiv);
        div.append(goodsListDiv);
        // 添加分类div到特定分类div
        $('.particular_type_div').append(div);
    }
    
    // 绑定点击事件
    $('.type_goods_list a.shop_sort').click(function () {
        var wsk = $(this).attr('id');
        var $all_product = $('.all_product');
        $.ajax({
            url: 'selectBySort.do',
            type: 'post',
            dataType: 'JSON',
            data: {sort: wsk},
            success: function (data) {
                $all_product.html('');
                if (data.length === 0) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<figure class='detail_product'>" +
                        "<input type='hidden' value= ''/>" +
                        "<img src='' title='暂时没有该分类的商品' />" +
                        "<span class='detail_product_name'></span><br/>" +
                        "<span class='detail_product_cost'></span><br/>" +
                        "<span class='detail_buy product_1'>加入购物车</span>" +
                        "</figure>" +
                        "</div>");
                }
                for (var i = 0; i < data.length; i++) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<div class='detail_product'>" +
                        "<input type='hidden' value=" + data[i].id + " '/>" +
                        "<div class='product_img_div'><img class='show_img' src='" + data[i].image + "' title='" + data[i].name + "'/></div>" +
                        "<p class='show_tip'>"+data[i].remark+"</p>"+
                        "<span class='detail_product_name' value='"+data[i].id+"'>" + data[i].name + "</span><br/>" +
                        "<span class='detail_product_cost'>￥" + data[i].price + "</span><br/>" +
                        "<span class='detail_buy product_1' value='"+data[i].id+"'>加入购物车</span>" +
                        "</div>" +
                        "</div>");
                }
                //进入查看商品的详情,通过id
                $('.detail_product_name').click(function () {
                    var id = $(this).attr('value');
                    window.location.href='/selectById.do?id='+id;
                });
                insertShopCar();
            }
        });
    });
}

$('.my_type_div').on('mouseleave', function() {
    setTimeout(hideParticular, 200);
});
$('.particular_type_div').on('mouseenter', function() {
    clearTimeout(window.hideTimer);
}).on('mouseleave', function() {
    setTimeout(hideParticular, 200);
});
    $('header').click(function () {
        hideParticular();
    });
    //new
    bindClick();
    //  直接点击页数
    function bindClick() {
        $('.pagination_div ul li').click(function () {
            var cur = $(".pagination_div ul li.current_page").children("a").html();
            $(".pagination_div ul li.current_page").removeClass("current_page");
            $(this).addClass("current_page");
            //  点击的页数
            var which_click = $(this).children("a").html();
            //  在if里面处理
            if (cur !== which_click) {
                selectByCounts(which_click);
            }
        });
    }

    //  上一页
    $('.pagination_lt').click(function () {
        var current = $('.pagination_div ul li.current_page');
        var temp = current.children("a").html();
        //  已经达到最左边，再点无反应
        if (temp == 1) {
            return false;
        }
        updateCurrent(current, 1, temp);
        //      这个就是当前的页数
        var current_page = $('.pagination_div ul li.current_page').children("a").html();
        selectByCounts(current_page);
    });
    //下一页
    $('.pagination_gt').click(function () {
        var current = $('.pagination_div ul li.current_page');
        var temp = current.children("a").html();
        // 到达最右边
        if (temp == 99) {
            return false;
        }
        updateCurrent(current, 2, temp);
        var current_page = $('.pagination_div ul li.current_page').children("a").html();
        //      通过这个current_page 来获取数据
        selectByCounts(current_page);

    });

    // temp 当前的值（1,2,3,4...）
    function updateCurrent(current, to, temp) {
        //    1左，2右
        var num = current.nextAll().length;
        if (to == 1) {
            if (num == 4) {
                current.siblings(":last").remove();
                current.before("<li><a>" + (temp - 1) + "</a></li>");
            }
            if (num == 3) {
                if (!(temp - 2 < 1)) {
                    current.siblings(":last").remove();
                    current.siblings(":first").before("<li><a>" + (temp - 2) + "</a></li>");
                }
            }
            current.removeClass("current_page");
            current.prev().addClass("current_page");
        } else {
            if (num == 0) {
                current.siblings(":first").remove();
                current.after("<li><a>" + (parseInt(temp) + 1) + "</a></li>");
            }
            if (num == 1) {
                current.siblings(":first").remove();
                current.siblings(":last").after("<li><a>" + (parseInt(temp) + 2) + "</a></li>");
            }
            current.removeClass("current_page");
            current.next().addClass("current_page");
        }
        bindClick();
    }
    function selectByCounts(currentCounts) {
        var $all_product = $('.all_product');
        $.ajax({
            url: 'selectByCounts.do',
            type: 'post',
            dataType: 'JSON',
            data: {counts: currentCounts},
            success: function (data) {
                $all_product.html('');
                if (data.length === 0) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<div class='detail_product'>" +
                        "<input type='hidden' value= ''/>" +
                        "<div class='product_img_div'><img src='' title='暂时没有该分类的商品' /></div>" +
                        "<span class='detail_product_name'></span><br/>" +
                        "<span class='detail_product_cost'></span><br/>" +
                        "<span class='detail_buy product_1'>加入购物车</span>" +
                        "</div>" +
                        "</div>");
                }
                for (var i = 0; i < data.length; i++) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<div class='detail_product'>" +
                        "<input type='hidden' value=" + data[i].id + " '/>" +
                        "<div class='product_img_div'>" +
                        "<img class='show_img' src='" + data[i].image + "' title='" + data[i].name + "'/>" +
                        "</div>" +
                        "<p class='show_tip'>"+data[i].remark+"</p>"+
                        "<span class='detail_product_name' value='"+data[i].id+"'>" + data[i].name + "</span><br/>" +
                        "<span class='detail_product_cost'>￥" + data[i].price + "</span><br/>" +
                        "<span class='detail_buy product_1' value='"+data[i].id+"'>加入购物车</span>" +
                        "</div>" +
                        "</div>");
                }
                //进入查看商品的详情,通过id
                $('.detail_product_name').click(function () {
                    var id = $(this).attr('value');
                    window.location.href='/selectById.do?id='+id;
                });
                insertShopCar();
            }
        });

    }
    //进入查看商品的详情,通过id
    $('.detail_product_name').click(function () {
        var id = $(this).attr('value');
        window.location.href='/selectById.do?id='+id;
    });
    function insertShopCar() {
        $('.detail_buy').click(function () {
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
    }

// hideParticular函数已在文件末尾重新定义，此处不再需要

// 获取分类列表函数
function getTypeList() {
    // 从缓存中获取分类数据
    if (window.type_list) {
        return window.type_list;
    }
    // 从服务器获取分类数据，使用POST请求
    $.post("/ajax/getAllKinds.do", null, function (data) {
        window.type_list = data;
        // 重新渲染一级分类列表
        renderFirstLevelCategories(data);
    }, "json").fail(function(xhr, status, error) {
        // console.error("获取分类数据失败:", error);
    });
    return [];
}

// 渲染一级分类列表
function renderFirstLevelCategories(categories) {
    var $typeList = $('.my_type_div ul');
    $typeList.empty();
    
    if (!categories || categories.length === 0) {
        $typeList.append("<li>暂无分类数据</li>");
        return;
    }
    
    for (var i = 0; i < categories.length; i++) {
        var catName = categories[i].kind || categories[i].name || '未命名分类';
        var catId = categories[i].id || '';
        $typeList.append("<li class='type_" + catId + "'><span>" + catName + "</span></li>");
    }
    
    // 全局变量记录当前展开的分类ID
    window.currentExpandedCategory = null;
    
    // 绑定click事件替代hover
    $('.my_type_div ul li').click(function () {
        $(this).css("background-color", "#f5f5f5");
        var pid = $(this).attr("class").split('_')[1];
        
        // 切换展开/收回状态
        if (window.currentExpandedCategory === pid) {
            // 如果点击的是当前展开的分类，则收起
            $('.particular_type_div').css("display", "none");
            window.currentExpandedCategory = null;
        } else {
            // 展开新分类
            addList(pid);
            $('.particular_type_div').css("display", "block");
            window.currentExpandedCategory = pid;
        }
    });
    
    // 移除鼠标移开时的背景色变化
    $('.my_type_div ul li').mouseleave(function () {
        // 只有当不是当前展开的分类时才恢复背景色
        var pid = $(this).attr("class").split('_')[1];
        if (window.currentExpandedCategory !== pid) {
            $(this).css("background-color", "#ffffff");
        }
    });
}

// 移除自动隐藏功能，改为点击控制
function hideParticular() {
    // 保留函数定义以避免引用错误，但不执行自动隐藏
    // 点击事件将控制显示/隐藏
    return;
}

}); // 闭合主函数$(function () {

