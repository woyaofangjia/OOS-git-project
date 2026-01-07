/**
 * Created by alone on 2017/5/18.
 */
$(document).ready(function () {
    // 为全选复选框添加点击事件
    $('#all').click(function () {
        // 获取当前全选复选框的状态
        var isChecked = $(this).prop('checked');
        // 将所有商品复选框设置为相同状态
        $('input[name="checkbox"]').prop('checked', isChecked);
        // 更新总价
        updateTotalPrice();
    });

    // 为商品复选框添加点击事件
    $(document).on('click', 'input[name="checkbox"]', function () {
        // 检查是否所有商品都被选中
        var allChecked = true;
        var hasCheckbox = false;
        
        $('input[name="checkbox"]').each(function () {
            hasCheckbox = true;
            if (!$(this).prop('checked')) {
                allChecked = false;
                return false; // 退出循环
            }
        });
        
        // 只有当存在复选框时才更新全选状态
        if (hasCheckbox) {
            $('#all').prop('checked', allChecked);
        } else {
            $('#all').prop('checked', false);
        }
        
        // 更新总价
        updateTotalPrice();
    });

    // 为加号按钮添加点击事件
    $(document).on('click', 'span.add:not(.disabled)', function () {
        var quantityElement = $(this).siblings('.number');
        var currentQuantity = parseInt(quantityElement.html());
        var newQuantity = currentQuantity + 1;
        var row = $(this).closest('tr');
        var sid = row.find('.deleteShopCar').attr('value');
        var stock = parseInt(row.attr('data-stock'));
        
        // 检查是否超过库存限制
        if (newQuantity > stock) {
            alert('商品数量已达上限，无法继续增加');
            return;
        }
        
        // 先更新界面显示
        quantityElement.html(newQuantity);
        updateSubtotal(row);
        updateTotalPrice();
        
        // 检查是否达到库存上限，如果达到则禁用加号按钮
        if (newQuantity >= stock) {
            $(this).addClass('disabled');
        }
        
        // 异步更新购物车数量
        updateShopCarQuantity(sid, newQuantity, quantityElement, currentQuantity);
    });

    // 为减号按钮添加点击事件
    $(document).on('click', 'span.minus', function () {
        var quantityElement = $(this).siblings('.number');
        var currentQuantity = parseInt(quantityElement.html());
        if (currentQuantity > 1) {
            var newQuantity = currentQuantity - 1;
            var row = $(this).closest('tr');
            var sid = row.find('.deleteShopCar').attr('value');
            var addButton = $(this).siblings('.add');
            
            // 先更新界面显示
            quantityElement.html(newQuantity);
            updateSubtotal(row);
            updateTotalPrice();
            
            // 如果减少数量后，加号按钮被禁用，则启用它
            addButton.removeClass('disabled');
            
            // 异步更新购物车数量
            updateShopCarQuantity(sid, newQuantity, quantityElement, currentQuantity);
        }
    });

    // 为地址选择添加事件
    var which = 0;
    $('.shipping_address').click(function () {
        var id = $(this).attr('id');
        $('.shipping_address').each(function () {
            if ($(this).attr('id')==id) {
                $(this).css({"border-color": "#c17c3a"});
                which = id;
            }else {
                $(this).css({"border-color": "rgba(0,0,0,0.1)"});
            }
        });
    });
    
    // 为铅笔图标添加点击事件，跳转到个人信息编辑页面
    $('.pencil_icon').click(function (e) {
        e.stopPropagation(); // 阻止事件冒泡，避免触发shipping_address的点击事件
        // 跳转到个人信息编辑页面
        window.location.href = '/personal_info.do';
    });

    // 更新小计
    function updateSubtotal(row) {
        var price = parseFloat(row.find('.cost span').html());
        var quantity = parseInt(row.find('.number').html());
        var subtotal = price * quantity;
        row.find('.per_sum span').html(returnFloat(subtotal));
    }

    // 更新总价
    function updateTotalPrice() {
        var totalPrice = 0;
        $('.cart_content table tr.table_content').each(function () {
            var isCheck = $(this).children("td.input_checkbox").children("input").is(":checked");
            if(isCheck){
                var subtotalText = $(this).children(".per_sum").children("span").html();
                if (subtotalText) {
                    totalPrice += parseFloat(subtotalText);
                }
            }
        });
        $('.end_pay').children(".all_sum").children("span").html(returnFloat(totalPrice));
    }

    // 异步更新购物车数量
    function updateShopCarQuantity(sid, quantity, quantityElement, originalQuantity) {
        $.ajax({
            url: '/updateShopCarQuantity.do',
            type: 'POST',
            data: {
                sid: sid,
                quantity: quantity
            },
            success: function (result) {
                if (result.code !== 200) {
                    // 更新失败，恢复原数量
                    quantityElement.html(originalQuantity);
                    updateSubtotal($(quantityElement).closest('tr'));
                    updateTotalPrice();
                    // 如果原数量小于库存，则确保加号按钮未被禁用
                    var row = $(quantityElement).closest('tr');
                    var stock = parseInt(row.attr('data-stock'));
                    var addButton = $(quantityElement).siblings('.add');
                    if (originalQuantity < stock) {
                        addButton.removeClass('disabled');
                    }
                    // 显示错误消息
                    alert('更新购物车数量失败: ' + (result.message || '未知错误'));
                } else {
                    // 更新成功，检查并更新按钮状态
                    var row = $(quantityElement).closest('tr');
                    var stock = parseInt(row.attr('data-stock'));
                    var addButton = $(quantityElement).siblings('.add');
                    var currentQuantity = parseInt(quantityElement.html());
                    
                    // 如果当前数量达到或超过库存，则禁用加号按钮
                    if (currentQuantity >= stock) {
                        addButton.addClass('disabled');
                    } else {
                        addButton.removeClass('disabled');
                    }
                }
            },
            error: function () {
                // 网络错误，恢复原数量
                quantityElement.html(originalQuantity);
                updateSubtotal($(quantityElement).closest('tr'));
                updateTotalPrice();
                // 如果原数量小于库存，则确保加号按钮未被禁用
                var row = $(quantityElement).closest('tr');
                var stock = parseInt(row.attr('data-stock'));
                var addButton = $(quantityElement).siblings('.add');
                if (originalQuantity < stock) {
                    addButton.removeClass('disabled');
                }
                alert('网络错误，更新购物车数量失败');
            }
        });
    }

    // 为删除按钮添加点击事件
    $(document).on('click', '.deleteShopCar', function () {
        var row = $(this).closest('tr');
        var id = $(this).attr('value');
        var sid = $(this).parent().siblings(".show_img").children().attr("value");
        
        if (confirm('确定要删除该商品吗？')) {
            $.ajax({
                url: 'deleteShopCar.do',
                dataType: 'JSON',
                type: 'post',
                data: {id: id, sid: sid},
                success: function (data) {
                    var result = data.result;
                    if (result==2){
                        alert('您还没有登录，请先登录');
                    }  else if (result==1) {
                        // 移除该行
                        row.remove();
                        alert("删除成功");
                        // 更新总价
                        updateTotalPrice();
                        // 检查购物车是否为空
                        checkEmptyCart();
                    } else {
                        alert('删除失败，请检测网络');
                    }
                },
                error: function () {
                    alert('网络错误，删除失败');
                }
            });
        }
    });

    // 检查购物车是否为空
    function checkEmptyCart() {
        var hasItems = $('.table_content').length > 0;
        if (!hasItems) {
            // 如果购物车为空，显示空购物车提示
            var emptyHtml = '<tr class="empty_cart_row"><td colspan="8" class="empty_cart"><p>您的购物车还是空的，快去<a href="/mall_page.do">逛逛吧~</a></p></td></tr>';
            $('.cart_content table').append(emptyHtml);
            $('#all').prop('checked', false);
        }
    }

    // 保留两位小数
    function returnFloat(value){
        var value=Math.round(parseFloat(value)*100)/100;
        var xsd=value.toString().split(".");
        if(xsd.length==1){
            value=value.toString()+".00";
            return value;
        }
        if(xsd.length>1){
            if(xsd[1].length<2){
                value=value.toString()+"0";
            }
            return value;
        }
    }

    // 页面加载时更新总价
    updateTotalPrice();
    // 检查购物车是否为空
    checkEmptyCart();
    
    // 初始化加号按钮状态
    $('.cart_content table tr.table_content').each(function() {
        var row = $(this);
        var stock = parseInt(row.attr('data-stock'));
        var currentQuantity = parseInt(row.find('.number').html());
        var addButton = row.find('.add');
        
        // 如果当前数量达到或超过库存，则禁用加号按钮
        if (currentQuantity >= stock) {
            addButton.addClass('disabled');
        }
    });
});
