/**
 * Created by alone on 2017/5/16.
 */

// 全局函数：初始化第一个下拉框（一级分类）
function initFirstSelect() {
    var firstSelect = $('.choose_first_type');
    firstSelect.empty(); // 清空现有选项
    firstSelect.append('<option value="">请选择一级分类</option>');
    
    // 确保分类数据已初始化
    if (typeof initTypeList === 'function') {
        initTypeList();
    }
    
    // 使用动态加载的一级分类数据
    if (window.allKindsCache && window.allKindsCache.length > 0) {
        // 如果有缓存的一级分类数据，使用缓存
        for (var i = 0; i < window.allKindsCache.length; i++) {
            var kind = window.allKindsCache[i];
            var option = $('<option>').attr('id', 'type_' + (i + 1)).val(kind.id).text(kind.name);
            if (i === 0) {
                option.attr('selected', 'selected');
            }
            firstSelect.append(option);
        }
    } else {
        console.warn('分类数据未加载，将尝试动态获取');
        // 动态加载一级分类数据
        $.ajax({
            url: '/getAllKinds.do',
            type: 'POST',
            dataType: 'json',
            success: function(data) {
                // 缓存加载的一级分类数据
                window.allKindsCache = data;
                
                // 填充一级分类选项
                for (var i = 0; i < data.length; i++) {
                    var kind = data[i];
                    // 使用后端返回的kind字段或name字段
                    var kindName = kind.kind || kind.name;
                    var option = $('<option>').attr('id', 'type_' + (i + 1)).val(kind.id).text(kindName);
                    if (i === 0) {
                        option.attr('selected', 'selected');
                    }
                    firstSelect.append(option);
                }
                
                // 更新二级分类
                if (data.length > 0) {
                    updateSecondSelect();
                }
            },
            error: function() {
                console.error('加载一级分类失败');
            }
        });
    }
}

// 全局函数：更新第二个下拉框（二级分类）
function updateSecondSelect() {
    // 更新第二个下拉框（二级分类）
    var secondSelect = $('.choose_second_type');
    secondSelect.empty(); // 清空现有选项
    secondSelect.append('<option value="">请选择二级分类</option>');
    
    // 清空三级分类下拉框
    $('.choose_third_type').empty();
    $('.choose_third_type').append('<option value="">请选择三级分类</option>');
    
    var firstSelect = $('.choose_first_type');
    var firstSelectVal = firstSelect.val();
    
    if (firstSelectVal) {
        // 确保 classificationCache 存在
        if (!window.classificationCache) {
            window.classificationCache = {};
        }
        
        if (window.classificationCache[firstSelectVal]) {
            // 使用缓存的二级分类数据
            var classifications = window.classificationCache[firstSelectVal];
            populateSecondSelect(classifications);
        } else {
            // 动态加载二级分类数据
            $.ajax({
                url: '/getClassification.do',
                type: 'POST',
                data: {id: firstSelectVal},
                dataType: 'json',
                cache: false,
                success: function(data) {
                    // 缓存加载的二级分类数据
                    window.classificationCache[firstSelectVal] = data;
                    populateSecondSelect(data);
                },
                error: function() {
                    console.error('加载二级分类失败');
                }
            });
        }
    }
    
    function populateSecondSelect(data) {
        if (data && data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                var classification = data[i];
                // 使用后端返回的name字段
                var classificationName = classification.name;
                var option = $('<option>').attr('id', (10000 + i)).val(classification.id).text(classificationName);
                secondSelect.append(option);
                
                // 如果没有指定选中项，默认选中第一个
                if (i == 0) {
                    option.attr('selected', 'selected');
                    // 更新三级分类
                    if (classification.content) {
                        updateThirdSelect(classification.content);
                    }
                }
            }
        }
    }
}

// 全局函数：更新第三个下拉框（三级分类）
function updateThirdSelect(content) {
    var thirdSelect = $('.choose_third_type');
    thirdSelect.empty(); // 清空现有选项
    thirdSelect.append('<option value="">请选择三级分类</option>');
    
    var secondSelectVal = $('.choose_second_type').val();
    
    if (secondSelectVal) {
        // 确保 specificCache 存在
        if (!window.specificCache) {
            window.specificCache = {};
        }
        
        if (window.specificCache[secondSelectVal]) {
            // 使用缓存的三级分类数据
            populateThirdSelect(window.specificCache[secondSelectVal]);
        } else {
            // 动态加载三级分类数据
            $.ajax({
                url: '/getSpecific.do',
                type: 'POST',
                data: {id: secondSelectVal},
                dataType: 'json',
                success: function(data) {
                    // 缓存加载的三级分类数据
                    window.specificCache[secondSelectVal] = data;
                    populateThirdSelect(data);
                },
                error: function() {
                    console.error('加载三级分类失败');
                    // 如果动态加载失败，尝试使用传入的 content 参数
                    if (content) {
                        populateThirdSelect(content);
                    }
                }
            });
        }
    } else if (content) {
        // 使用传入的 content 参数（兼容性处理）
        populateThirdSelect(content);
    }
    
    function populateThirdSelect(data) {
        if (!data || data.length === 0) return;
        
        // 检查是否为多级分类结构
        if(data[0].content) {
            // 如果是多级分类，先显示第一级子分类
            for(var j = 0; j < data.length; j++) {
                var rel_type = data[j];
                var relTypeName = rel_type.name;
                var option = $('<option>').attr('id', relTypeName).val('category_' + j).text(relTypeName);
                if(j === 0) {
                    option.attr('selected', 'selected');
                }
                thirdSelect.append(option);
            }
        } else {
            // 普通分类，直接显示具体类别
            for(var j = 0; j < data.length; j++) {
                var rel_type = data[j];
                var relTypeName = rel_type.name;
                var option = $('<option>').attr('id', rel_type.id).val(rel_type.id).text(relTypeName);
                if(j === 0) {
                    option.attr('selected', 'selected');
                }
                thirdSelect.append(option);
            }
        }
    }
}

// 全局函数：清除分类缓存
function clearTypeListCache() {
    window.allKindsCache = null;
    window.classificationCache = {};
    window.specificCache = {};
}

$(function () {
    // 初始化分类数据
    var curFirst = 0;
    var curSecond = 0;
    
    // 清除可能存在的旧分类缓存
    clearTypeListCache();
    
    // 确保 type_list_obj.js 中的函数可用
    if (typeof initTypeList !== 'function') {
        console.warn('type_list_obj.js 未加载，尝试直接通过API加载分类数据');
    }
    
    // 初始化第一个下拉框
    initFirstSelect();
    updateSecondSelect();
    
    // 如果是编辑页面，需要根据已有的分类ID选中对应的选项
    var existingSortId = $('input[name="id"]').val();
    if (existingSortId && existingSortId !== '0') {
        // 编辑模式下，尝试从后端获取分类ID并选中
        setTimeout(function() {
            var sortId = $('select[name="sort"]').val();
            if (sortId) {
                // 如果sortId已存在，则尝试找到对应的分类层级
                findAndSelectClassification(sortId);
            }
        }, 100);
    }
    
    /**
     * 根据分类ID查找并选中对应的分类层级
     */
    function findAndSelectClassification(sortId) {
        // 清空分类缓存，强制重新加载最新数据
        clearTypeListCache();
        initFirstSelect();
    }
    
    $('.title_input').bind('focus',function () {
        $(this).animate({width: "60%"}, 500);
    });
    
    $('.title_input').bind('blur',function () {
        $(this).animate({width: "27%"}, 500);
    });
    
    $('.detail_textarea').bind('focus',function () {
        $(this).animate({width: "60%", height: "8em"}, 500);
    });
    
    $('.detail_textarea').bind('blur',function () {
        if ($(this).val() == ''){
            $(this).animate({width: "27%", height: "5em"}, 500);
        }
    });
    
    var temp = 1;
    $(".upload_img_input").change(function(){
        // 未选择文件时直接返回，避免createObjectURL报错
        if (!this.files || this.files.length === 0) {
            return;
        }
        var objUrl = getObjectURL(this.files[0]);
        if (!objUrl) {
            return;
        }
        $('#show_choose_img').attr("src", objUrl);
        $('#show_choose_img').css({opacity: 0});
        $('#show_choose_img').show(0).animate({opacity:1},1000);
        $('.existing_show_img').hide();
    });
    // 删除已选图片
    $(".delete_img_btn").click(function (e) {
        e.preventDefault();
        var $input = $(".upload_img_input");
        $input.val("");
        $('#show_choose_img').attr("src", "").hide();
        $('.existing_show_img').show();
    });
    
    // 获取图片的url。是临时文件
    function getObjectURL(file) {
        if (!file) {
            return null;
        }
        var url = null ;
        if (window.createObjectURL!=undefined) { // basic
            url = window.createObjectURL(file) ;
        } else if (window.URL!=undefined) { // mozilla(firefox)
            url = window.URL.createObjectURL(file) ;
        } else if (window.webkitURL!=undefined) { // webkit or chrome
            url = window.webkitURL.createObjectURL(file) ;
        }
        return url ;
    }
    
    $('.choose_first_type').change(function () {
        // 获取选中的一级分类索引
        var selectedIndex = $(this).prop('selectedIndex');
        curFirst = selectedIndex;
        curSecond = 0;
        updateSecondSelect();
    });
    
    $('.choose_second_type').change(function () {
        var getSelect = $(this).children('option:selected').attr("id");
        curSecond = (getSelect-10000);
        updateThirdSelect();
    });
    
    // 添加表单提交验证
    $('form[action="/insertGoods.do"]').on('submit', function(e) {
        // 添加调试日志
        console.log('开始表单验证...');
        
        // 检查分类选择
        var category1 = $('.choose_first_type').val();
        var category2 = $('.choose_second_type').val();
        var category3 = $('.choose_third_type').val();
        var sort = $('.choose_third_type').val(); // 获取实际提交的sort值
        
        console.log('分类选择值:', category1, category2, category3);
        console.log('提交的sort值:', sort);
        
        if (!category1 || !category2 || !category3 || category1 === '' || category2 === '' || category3 === '' || !sort || sort === '') {
            e.preventDefault();
            alert('请选择完整的商品分类！');
            return false;
        }
        
        // 确保sort值是有效的数字且大于0
        if (isNaN(parseInt(sort)) || parseInt(sort) <= 0) {
            e.preventDefault();
            alert('请选择有效的商品分类！');
            return false;
        }
        
        // 验证其他必填字段
        var name = $('input[name="name"]').val();
        var remark = $('textarea[name="remark"]').val();
        var price = $('input[name="price"]').val();
        var quantity = $('input[name="quantity"]').val();
        var level = $('select[name="level"]').val();
        var image = $('input[name="image"]')[0].files.length;
        var action = $('input[name="action"]').val();
        
        // 添加所有字段的调试日志
        console.log('表单字段值:');
        console.log('商品名称:', name, '长度:', name ? name.length : 0);
        console.log('商品详情:', remark, '长度:', remark ? remark.length : 0);
        console.log('商品价格:', price, '数值:', parseFloat(price));
        console.log('商品数量:', quantity, '数值:', parseInt(quantity));
        console.log('商品成色:', level, '数值:', parseInt(level));
        console.log('图片数量:', image);
        console.log('操作类型:', action);
        
        if (!name || name.trim() === '') {
            e.preventDefault();
            alert('请输入商品名称！');
            return false;
        }
        
        if (name.length > 25) {
            e.preventDefault();
            alert('商品名称长度不能超过25个字符！');
            return false;
        }
        
        if (!remark || remark.trim() === '') {
            e.preventDefault();
            alert('请输入商品详情！');
            return false;
        }
        
        if (remark.length > 255) {
            e.preventDefault();
            alert('商品详情长度不能超过255个字符');
            return false;
        }
        
        if (!price || parseFloat(price) <= 0) {
            e.preventDefault();
            alert('请输入有效的商品价格（必须大于0）！');
            return false;
        }
        
        if (!quantity || parseInt(quantity) <= 0) {
            e.preventDefault();
            alert('请输入有效的商品数量（必须大于0）！');
            return false;
        }
        
        if (!level || parseInt(level) <= 0) {
            e.preventDefault();
            alert('请选择商品成色！');
            return false;
        }
        
        // 如果是新增商品（action=1），需要验证图片
        var action = $('input[name="action"]').val();
        if (action === '1' && image === 0) {
            e.preventDefault();
            alert('请选择商品图片！');
            return false;
        }
    });
});

// 检查提示信息 - 修改为只有在实际有验证错误信息时才显示
$(function() {
    // 只有当.show_tip元素存在验证错误信息时才显示提示
    // 避免页面加载时自动弹出验证错误
    var tipElement = $('.show_tip');
    if (tipElement.length > 0 && tipElement.text().trim() !== '') {
        alert(tipElement.text());
    }
})