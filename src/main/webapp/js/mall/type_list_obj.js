/**
 * Created by alone on 2017/5/15.
 * 更新于 2024/5/20 - 实现分类数据从后端动态加载，保持前后端一致性
 */

// 分类数据缓存
window.allKindsCache = null;
window.classificationCache = {};
window.specificCache = {};
// 修改为全局变量，确保缓存清除和重新初始化生效
window.type_list = [];
window.isInitialized = false;

/**
 * 清除分类数据缓存，强制重新从后端获取
 */
function clearTypeListCache() {
    window.allKindsCache = null;
    window.classificationCache = {};
    window.specificCache = {};
    window.type_list = [];
    window.isInitialized = false;
}

/**
 * 从后端初始化分类数据
 */
function initTypeList() {
    try {
        // 强制清除缓存并重新初始化，确保获取最新的分类数据
        clearTypeListCache();
        
        // console.log('开始初始化分类数据...');
        
        // 获取一级分类
        $.ajax({
            url: '/getAllKinds.do',
            type: 'GET',
            dataType: 'json',
            async: false, // 同步加载，确保页面渲染时有数据
            cache: false, // 禁用浏览器缓存，确保获取最新数据
            success: function(data) {
                // console.log('获取一级分类数据成功，数据长度:', data ? data.length : 0);
                
                if (!data || !Array.isArray(data)) {
                    // console.error('一级分类数据格式错误，使用降级方案');
                    initFallbackTypeList();
                    return;
                }
                
                // 按排序字段排序
                if (data[0] && typeof data[0].sort === 'number') {
                    data.sort(function(a, b) {
                        return a.sort - b.sort;
                    });
                }
                
                // 直接使用kind字段与后端保持一致
                window.allKindsCache = data.map(function(kind) {
                    // 直接使用kind字段，确保名称不为空
                    const categoryName = kind && kind.kind !== undefined && kind.kind !== null ? kind.kind : '未知分类';
                    return {
                        id: kind && kind.id !== undefined ? kind.id : 0,
                        name: categoryName, // 前端显示仍使用name属性，保持界面一致性
                        kind: kind.kind // 保留原始kind字段，与后端保持一致
                    };
                });
                
                window.type_list = [];
                
                // 为每个一级分类初始化二级分类数组
                for (var i = 0; i < window.allKindsCache.length; i++) {
                    window.type_list[i] = [];
                }
                
                // 依次获取每个一级分类下的二级分类
                for (var i = 0; i < data.length; i++) {
                    var pid = data[i] && data[i].id ? data[i].id : 0;
                    if (pid > 0) {
                        loadClassifications(pid, i);
                    }
                }
                
                window.isInitialized = true;
                // console.log('分类数据初始化完成');
            },
            error: function(xhr, status, error) {
                // console.error('加载一级分类失败:', error, '状态码:', xhr.status);
                // console.error('响应内容:', xhr.responseText);
                // 如果加载失败，使用降级方案
                initFallbackTypeList();
            }
        });
    } catch (e) {
        // console.error('初始化分类列表异常:', e);
        // 捕获所有异常，确保使用降级方案
        initFallbackTypeList();
    }
}

/**
 * 加载指定一级分类下的二级分类
 */
function loadClassifications(pid, index) {
        $.ajax({
            url: '/getClassification.do',
            type: 'POST', // 修改为POST请求，与后端保持一致
            data: {pid: pid}, // 参数名应为pid，与后端Mapper接口匹配
            dataType: 'json',
            async: false,
            cache: false, // 禁用浏览器缓存，确保获取最新数据
            success: function(data) {
                // 按排序字段排序
                if (data && Array.isArray(data) && data[0] && typeof data[0].sort === 'number') {
                    data.sort(function(a, b) {
                        return a.sort - b.sort;
                    });
                }
                
                window.classificationCache[pid] = data;
                
                // 为每个二级分类创建类型对象并加载三级分类
                for (var i = 0; i < data.length; i++) {
                    var classification = data[i];
                    var cid = classification.id;
                    
                    // 处理分类名称，确保与后端Classification.name字段对应
                    var className = classification.name !== undefined ? classification.name : '未知分类';
                    
                    // 创建二级分类类型
                    var secondType = createType(className, []);
                    window.type_list[index].push(secondType);
                    
                    // 加载三级分类
                    loadSpecifics(cid, secondType);
                }
            },
            error: function(xhr, status, error) {
            // console.error('加载二级分类失败，pid=' + pid + ':', error);
            // console.error('响应内容:', xhr.responseText);
        }
    });
}

/**
 * 加载指定二级分类下的三级分类
 */
function loadSpecifics(cid, parentType) {
    $.ajax({
        url: '/getSpecific.do',
        type: 'POST',
        data: {id: cid},
        dataType: 'json',
        async: false,
        cache: false, // 禁用浏览器缓存，确保获取最新数据
        success: function(data) {
            // 按排序字段排序
            if (data && Array.isArray(data) && data[0] && typeof data[0].sort === 'number') {
                data.sort(function(a, b) {
                    return a.sort - b.sort;
                });
            }
            
            window.specificCache[cid] = data;
            
            // 填充三级分类数据
            var specifics = [];
            for (var i = 0; i < data.length; i++) {
                var specific = data[i];
                // 处理三级分类名称，确保与后端Specific.name字段对应
                var specificName = specific.name !== undefined ? specific.name : '未知分类';
                specifics.push(createObject(specific.id, specificName));
            }
            parentType.content = specifics;
        },
        error: function(xhr, status, error) {
            // console.error('加载三级分类失败，cid=' + cid + ':', error);
        }
    });
}

/**
 * 降级方案：如果后端API调用失败，使用与数据库一致的默认分类
 */
function initFallbackTypeList() {
    // console.warn('无法获取分类数据，将显示与数据库一致的默认分类');
    // 与数据库 SQL/update_classification.sql 保持一致的本地数据
    // 严格按照SQL/update_classification.sql中的新版分类信息定义降级方案数据
    var fallbackStructure = [
        {
            id: 1,
            name: '电子数码',
            children: [
                {id: 1, name: '手机配件', items: [
                    {id: 1, name: '手机壳'},
                    {id: 2, name: '手机膜'},
                    {id: 3, name: '充电器'},
                    {id: 4, name: '耳机'},
                    {id: 5, name: '其他手机配件'}
                ]},
                {id: 2, name: '电脑配件', items: [
                    {id: 6, name: '键盘'},
                    {id: 7, name: '鼠标'},
                    {id: 8, name: '耳机'},
                    {id: 9, name: 'U盘'},
                    {id: 10, name: '移动硬盘'},
                    {id: 11, name: '其他电脑配件'}
                ]},
                {id: 3, name: '数码设备', items: [
                    {id: 12, name: '手机'},
                    {id: 13, name: '平板'},
                    {id: 14, name: '电脑'},
                    {id: 15, name: '相机'},
                    {id: 16, name: '其他数码设备'}
                ]}
            ]
        },
        {
            id: 2,
            name: '图书文具',
            children: [
                {id: 4, name: '专业书籍', items: [
                    {id: 17, name: '文学类'},
                    {id: 18, name: '理学类'},
                    {id: 19, name: '工学类'},
                    {id: 20, name: '农学类'},
                    {id: 21, name: '医学类'},
                    {id: 22, name: '经济学'},
                    {id: 23, name: '管理学'},
                    {id: 24, name: '法学类'},
                    {id: 25, name: '教育学'},
                    {id: 26, name: '其他专业书籍'}
                ]},
                {id: 5, name: '教材教辅', items: [
                    {id: 27, name: '专业课教材'},
                    {id: 28, name: '公共课教材'},
                    {id: 29, name: '考研资料'},
                    {id: 30, name: '考证资料'},
                    {id: 31, name: '其他教材教辅'}
                ]},
                {id: 6, name: '课外读物', items: [
                    {id: 32, name: '小说'},
                    {id: 33, name: '散文'},
                    {id: 34, name: '诗歌'},
                    {id: 35, name: '历史'},
                    {id: 36, name: '哲学'},
                    {id: 37, name: '其他课外读物'}
                ]},
                {id: 7, name: '文具用品', items: [
                    {id: 38, name: '笔类'},
                    {id: 39, name: '笔记本'},
                    {id: 40, name: '文件夹'},
                    {id: 41, name: '计算器'},
                    {id: 42, name: '其他文具'}
                ]}
            ]
        },
        {
            id: 3,
            name: '生活百货',
            children: [
                {id: 8, name: '生活用品', items: [
                    {id: 43, name: '收纳用品'},
                    {id: 44, name: '清洁用品'},
                    {id: 45, name: '家居装饰'},
                    {id: 46, name: '其他生活用品'}
                ]},
                {id: 9, name: '小家电', items: [
                    {id: 47, name: '台灯'},
                    {id: 48, name: '风扇'},
                    {id: 49, name: '吹风机'},
                    {id: 50, name: '其他小家电'}
                ]},
                {id: 10, name: '美妆个护', items: [
                    {id: 51, name: '护肤品'},
                    {id: 52, name: '化妆品'},
                    {id: 53, name: '个人护理'},
                    {id: 54, name: '其他美妆个护'}
                ]}
            ]
        },
        {
            id: 4,
            name: '服装鞋包',
            children: [
                {id: 11, name: '服装', items: [
                    {id: 55, name: '上衣'},
                    {id: 56, name: '裤子'},
                    {id: 57, name: '裙子'},
                    {id: 58, name: '外套'},
                    {id: 59, name: '其他服装'}
                ]},
                {id: 12, name: '鞋包', items: [
                    {id: 60, name: '鞋子'},
                    {id: 61, name: '背包'},
                    {id: 62, name: '钱包'},
                    {id: 63, name: '其他鞋包'}
                ]},
                {id: 13, name: '配饰', items: [
                    {id: 64, name: '帽子'},
                    {id: 65, name: '围巾'},
                    {id: 66, name: '手套'},
                    {id: 67, name: '其他配饰'}
                ]}
            ]
        },
        {
            id: 5,
            name: '运动健身',
            children: [
                {id: 14, name: '运动器材', items: [
                    {id: 68, name: '球类'},
                    {id: 69, name: '健身器材'},
                    {id: 70, name: '瑜伽用品'},
                    {id: 71, name: '其他运动器材'}
                ]},
                {id: 15, name: '运动服饰', items: [
                    {id: 72, name: '运动衣'},
                    {id: 73, name: '运动鞋'},
                    {id: 74, name: '运动配件'},
                    {id: 75, name: '其他运动服饰'}
                ]}
            ]
        },
        {
            id: 6,
            name: '其他商品',
            children: [
                {id: 16, name: '其他物品', items: [
                    {id: 76, name: '其他物品'}
                ]}
            ]
        }
    ];

    // 同步设置缓存对象，保持与后端接口结构一致
    window.allKindsCache = fallbackStructure.map(function(kind) {
        return {id: kind.id, name: kind.name};
    });
    window.classificationCache = {};
    window.specificCache = {};
    window.type_list = [];

    fallbackStructure.forEach(function(kind) {
        var secondList = [];
        var classificationList = [];

        kind.children.forEach(function(cls) {
            var specifics = cls.items.map(function(item) {
                return createObject(item.id, item.name);
            });
            window.specificCache[cls.id] = specifics;

            classificationList.push({
                id: cls.id,
                name: cls.name,
                content: specifics
            });

            secondList.push(createType(cls.name, specifics));
        });

        window.classificationCache[kind.id] = classificationList;
        window.type_list.push(secondList);
    });

    window.isInitialized = true;
}
function createObject(id, name) {
    var temp = new Object();
    temp.id = id;
    temp.name = name;
    return temp;
}
function createType(name, content) {
    var temp = new Object();
    temp.name = name;
    temp.content = content;
    return temp;
}
function getTypeList() {
    // 确保分类数据已初始化
    initTypeList();
    return window.type_list;
}

// 监听页面加载事件，确保每次页面刷新都能获取最新分类数据
$(document).ready(function() {
    clearTypeListCache();
    initTypeList();
});