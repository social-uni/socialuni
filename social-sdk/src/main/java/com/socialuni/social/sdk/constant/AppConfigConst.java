package com.socialuni.social.sdk.constant;


import com.socialuni.social.constant.DateTimeType;
import com.socialuni.social.constant.OpenDataQueryType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AppConfigConst {
    public static final String talkShowAdIntervalKey = "广告展示数量间隔";
    public static final String talkShowAdCountKey = "动态页广告展示次数";
    public static final String talkShowAdIndexListKey = "动态页展示广告索引列表";

    //用户可以获取几次验证码
    public static final String authCodeCountKey = "用户可以获取几次验证码";
    public static final String authCodeIpCountKey = "用户IP可以获取几次验证码";
    public static final String authCodePhoneCountKey = "手机号可以获取几次验证码";
    //验证码有效时间多少秒
    public static final String authCodeValidMinuteKey = "验证码有效时间多少分";

    public static final Map<String, Object> appConfigMap = new HashMap<String, Object>(){{
        put(AppConfigConst.talkShowAdIntervalKey, 8);
        put(AppConfigConst.talkShowAdCountKey, 10);
        put(AppConfigConst.authCodeCountKey, 30);
        put(AppConfigConst.authCodeIpCountKey, 200);
        put(AppConfigConst.authCodePhoneCountKey, 30);
        put(AppConfigConst.authCodeValidMinuteKey, 30);
    }};


    public static final Integer qingchiDevId = 1;
    public static final String appConfig = "appConfig";

    public static final String appName = "清池";
    public static final Long qingChiDevNum = 1212121212L;
    public static final String appMarketUrl = "market://details?id=com.qingchiapp";

    public static final String weibo_account_key = "微博账号";
    public static final String img_content = "图片内容";
    public static final String wx_account_key = "微信账号";
    public static final String qq_account_key = "qq账号";
    public static final String errorCode605ContactService = "605用户被封禁提示";
    public static final String errorCode604SystemKey = "604系统异常提示";
    public static final String errorCode601UnLogin = "601未登录提示";
    public static final String rewardedAdLimit = "每天观看激励视频限制次数";


    //获取联系方式消费贝壳数量
    public static final String contactExpenseShellKey = "contactExpenseShell";
    public static final String openChatExpenseShellKey = "开启会话需要支付的贝壳数量";
    //系统抽成比例
    public static final String sysServiceReceiveRatioKey = "sysServiceReceiveRatio";

    public static final String appHotUpdateVersionKey = "appHotUpdateVersion";
    public static final String appAppUpdateVersionKey = "appAppUpdateVersion";
    public static final String updateModeKey = "updateMode";


    //审核成功分
    public static final String auditSuccessKey = "举报成功奖励分数";
    public static final Integer auditSuccessValue = 10;
    //审核类型不一致分
    /*public static final String reportTypeMistakeKey = "审核成功奖励分数";
    public static final Integer reportTypeMistakeValue = 5;*/
    //举报错误扣除分
    public static final String auditRefuseKey = "举报错误扣除分数";
    public static final Integer reportErrorValue = 20;
    //低于多少分禁止举报
    public static final String cantReportKey = "低于多少分禁止举报";
    public static final Integer cantReportValue = -100;
    //低于多少分限制举报
    public static final String limitReportKey = "低于多少分限制举报";
    public static final Integer limitReportValue = -10;
    //限制每天高质量举报次数
    public static final String highLimitReportCountKey = "限制每天高质量举报次数";
    public static final Integer highLimitReportCount = 20;
    //限制每天低质量举报次数
    public static final String lowLimitReportCountKey = "限制每天低质量举报次数";
    public static final Integer lowLimitReportCount = 2;
    //被多少人举报则隐藏
    public static final String reportCountHideKey = "被举报几次隐藏";
    public static final Integer reportCountHide = 1;
    //验证码间隔多少秒
    public static final String authCodeIntervalKey = "验证码间隔多少秒";




    public static final String notify_skip_page = "/pagesLazy/talk/talkDetail?talkId=";

    public static final String vipPriceKey = "vipPrice";

    public static Map<String, Long> unionIdTypeValidTimeMap = new HashMap<String, Long>() {{
        put(OpenDataQueryType.noDivId, (30L * DateTimeType.minute));
        put(OpenDataQueryType.noUserId, (30L * DateTimeType.minute));
        put(OpenDataQueryType.hasUserId, (30L * DateTimeType.minute));
    }};
}
