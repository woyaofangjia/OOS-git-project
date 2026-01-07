$( function() {
    $( "#accordion" ).accordion();
    $('.update_button').click(function () {
        var token = $('.token').val();
        var type = $(this).siblings(".first_info").children("input").attr("type");
        var which_update = $(this).siblings(".first_info").children("input").attr("class");
        var my_this = $(this);
        if (type=="radio") {
//                    单选按钮
            // 获取选中的单选按钮的值
            var value = $(this).siblings(".first_info").children("input[type='radio']:checked").val();
            var redirectUrl = $('.redirectUrl').val();
            
            // 创建数据对象
            var dataObj = {};
            dataObj.gender = value; // 这里需要将1/2映射为1/0
            dataObj.token = token;
            if (redirectUrl) {
                dataObj.redirectUrl = redirectUrl;
            }
            
            $.ajax({
                url:'/certification.do',
                type:'post',
                dataType:'JSON',
                data: dataObj,
                success:function (data) {
                    // 检查响应中是否有新token，如果有则更新前端存储的token
                    if (data.newToken) {
                        $('.token').val(data.newToken);
                        console.log('Updated token to: ' + data.newToken);
                    }
                    
                    // 处理错误信息
                    if (data.error) {
                        alert(data.error);
                        return;
                    }
                    
                    var result = data.result;
                    if (result === 0){
                        alert('更新失败，请检测信息格式');
                    } else if (result === 1){
                        // 根据值显示对应的性别文本
                        var genderText = (value === '1') ? '男' : '女';
                        alert('更新成功');
                        my_this.parent().prev().children().html(genderText);
                        
                        // 检查是否需要重定向
                        if (data.redirectUrl) {
                            setTimeout(function() {
                                window.location.href = data.redirectUrl;
                            }, 1000); // 1秒后重定向，让用户有时间看到成功提示
                        }
                    }
                }
            });
        }else {
            var val = $(this).siblings(".first_info").children("input").val();
            if (val==undefined||val=='') {
                $(this).siblings(".first_info").children(".reqiure_enter").show(0);
            }else{
//                        修改，修改类名或id，直接获取类名就可以
                var value =$(this).siblings(".first_info").children("input").val();
                var redirectUrl = $('.redirectUrl').val();
                // 对所有参数进行URL编码，确保特殊字符和中文正确传输
                // 创建数据对象而不是字符串，让jQuery正确处理contentType
                var dataObj = {};
                dataObj[which_update] = value;
                dataObj.token = token;
                if (redirectUrl) {
                    dataObj.redirectUrl = redirectUrl;
                }
                
                $.ajax({
                    url:'/certification.do',
                    type:'post',
                    dataType:'JSON',
                    data: dataObj,
                    success:function (data) {
                        // 检查响应中是否有新token，如果有则更新前端存储的token
                        if (data.newToken) {
                            $('.token').val(data.newToken);
                            console.log('Updated token to: ' + data.newToken);
                        }
                        
                        // 处理错误信息
                        if (data.error) {
                            alert(data.error);
                            return;
                        }
                        
                        var result = data.result;
                        if (result === 0){
                            alert('更新失败，请检测信息格式');
                        } else if (result === 1){
                            // $(this).text(value);
                            alert('更新成功');
                            updateText(value);
                            
                            // 检查是否需要重定向
                            if (data.redirectUrl) {
                                setTimeout(function() {
                                    window.location.href = data.redirectUrl;
                                }, 1000); // 1秒后重定向，让用户有时间看到成功提示
                            }
                        }
                        if (which_update === 'userName'){
                            $('.user_name_a').text(value);
                        }
                    }
                });
            }
        }
        function updateText(value) {
            my_this.parent().prev().children().html(value);
        }
    });
//            实时监听输入框的输入变化，当有输入值的时候，隐藏必须填写字段
    $('.first_info input').bind("input propertychange change",function () {
        var val = $(this).val();
        if (val!=undefined&&val!='') {
            $(this).siblings(".reqiure_enter").hide(0);
        }
    });
} );
$(function () {
    // 检查是否有提示信息
    if ($('.show_tip').length > 0 && $('.show_tip').text().trim() !== ''){
        // 已经在页面中显示了提示信息，这里不再重复弹出alert
        // 可以添加一些其他效果，比如高亮显示或自动消失
        setTimeout(function() {
            $('.show_tip').fadeIn('slow');
        }, 500);
    }
});