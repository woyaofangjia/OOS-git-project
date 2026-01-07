// 标签切换功能
function showTab(tabId) {
    // 隐藏所有内容
    var tabContents = document.getElementsByClassName('tab_content');
    for (var i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = 'none';
    }
    
    // 移除所有按钮的active类
    var tabButtons = document.getElementsByClassName('tab_button');
    for (var i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove('active');
    }
    
    // 显示选中的内容和按钮
    document.getElementById(tabId).style.display = 'block';
    event.currentTarget.classList.add('active');
}

// 页面加载完成后绑定事件
$(document).ready(function() {
    // 绑定审核通过按钮点击事件
    $('.audit_approve').click(function() {
        var productId = $(this).attr('data-id');
        var confirmMsg = '确定要通过这个商品的审核吗？';
        
        if (confirm(confirmMsg)) {
            auditProduct(productId, 1); // 1表示通过
        }
    });
    
    // 绑定审核拒绝按钮点击事件
    $('.audit_reject').click(function() {
        var productId = $(this).attr('data-id');
        var confirmMsg = '确定要拒绝这个商品的审核吗？';
        
        if (confirm(confirmMsg)) {
            auditProduct(productId, 2); // 2表示拒绝
        }
    });
});

// 审核商品的AJAX请求
function auditProduct(productId, auditStatus) {
    $.ajax({
        url: '/audit_product.do',
        type: 'POST',
        data: {
            'id': productId,
            'auditStatus': auditStatus
        },
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                alert(response.message);
                // 刷新页面以更新列表
                location.reload();
            } else {
                alert('审核失败：' + response.message);
            }
        },
        error: function() {
            alert('网络错误，请稍后重试');
        }
    });
}

// 为动态加载的内容重新绑定事件
function rebindEvents() {
    // 重新绑定审核通过按钮点击事件
    $('.audit_approve').off('click').on('click', function() {
        var productId = $(this).attr('data-id');
        var confirmMsg = '确定要通过这个商品的审核吗？';
        
        if (confirm(confirmMsg)) {
            auditProduct(productId, 1); // 1表示通过
        }
    });
    
    // 重新绑定审核拒绝按钮点击事件
    $('.audit_reject').off('click').on('click', function() {
        var productId = $(this).attr('data-id');
        var confirmMsg = '确定要拒绝这个商品的审核吗？';
        
        if (confirm(confirmMsg)) {
            auditProduct(productId, 2); // 2表示拒绝
        }
    });
}