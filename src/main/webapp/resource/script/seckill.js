// 存放交互逻辑js代码
// JavaScript模块化

var seckill = {
    // 封装秒杀相关的ajax的url
    URL: {
        getTime : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        killUrl : function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    // 验证手机号函数
    validatePhone: function (phone) {
        if (phone && phone.length === 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    // 秒杀操作
    handleSeckill: function (seckillId, node) {
        // 获取秒杀地址，控制显示按钮，执行秒杀
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        //发送post请求
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            // 回调函数中执行交互流程
            if (result && result['success']) {
                // 成功拿到exposer对象
                var exposer = result['data'];
                if (exposer['exposed']) {
                    // 秒杀开启标志为true，根据md5拼接秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.killUrl(seckillId,md5);
                    // 控制台打印killUrl
                    console.log('killUrl:'+killUrl);
                    // 绑定一次click事件
                    $('#killBtn').one('click', function () {
                        // 执行秒杀操作
                        //  -禁用按钮
                        $(this).addClass('disable');
                        //  -发送post请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                // 秒杀操作成功，显示其返回信息
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    // 由于服务器和客户端时间存在差异，从服务器更新系统时间
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result:' + result);
            }
        });
    },
    countDown: function (seckillId, nowTime, startTime, endTime) {
        console.log('now:'+nowTime+'-start:'+startTime+'-end:'+endTime);
        var seckillBox = $('#seckill-box');
        if (nowTime>endTime){
            // 秒杀结束
            seckillBox.html('秒杀已经结束');
        } else if (nowTime<startTime){
            // 秒杀未开启，显示倒计时
            // 绑定countdown插件事件
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function (event) {
                // 指定时间显示格式
                var format = event.strftime('秒杀倒计时：%d天 %H时-%M分-%S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function (event) {
                // 秒杀操作
                seckill.handleSeckill(seckillId,seckillBox);
            });
        } else{
            // 秒杀操作
            seckill.handleSeckill(seckillId,seckillBox);
        }
    },
    // 详情页秒杀逻辑
    detail: {
        // 初始化方法
        init: function (params) {
            // alert("here");
            //手机号登录、计时交互
            // 在cookie中找到手机号
            var killPhone = $.cookie('killPhone');

            // 验证手机号，当手机号不满足要求时弹出模态对话框
            if (!seckill.validatePhone(killPhone)) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //用户输入的电话号码有效，写入cookie并刷新页面
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            // 已经登录
            // 计时器逻辑
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            $.get(seckill.URL.getTime(),{},function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    // 判断时间，进行计时器交互操作
                    seckill.countDown(seckillId,nowTime,startTime,endTime);
                } else {
                    console.log('result='+result);
                }
            });
        }
    }
}