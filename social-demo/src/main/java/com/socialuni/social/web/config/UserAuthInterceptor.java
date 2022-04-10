package com.socialuni.social.web.config;

import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.exception.constant.ErrorCode;
import com.socialuni.social.exception.constant.ErrorType;
import com.socialuni.social.web.sdk.model.RequestLogDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.exception.SocialNotLoginException;
import com.socialuni.social.exception.SocialSystemException;
import com.socialuni.social.sdk.constant.ErrorMsg;
import com.socialuni.social.web.sdk.utils.RequestLogUtil;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import com.socialuni.social.sdk.utils.RedisUtil;
import com.socialuni.social.web.sdk.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@Slf4j
public class UserAuthInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtil redisUtil;

    /*
     * 进入controller层之前拦截请求
     * 在请求处理之前进行调用（Controller方法调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse res, Object o) {
        Date startTime = new Date();

        RequestLogDO requestLogDO = new RequestLogDO();
        UserDO user = SocialUserUtil.getMineUserAllowNull();
        if (user != null) {
            requestLogDO.setUserId(user.getId());
        }
        String requestIp = IpUtil.getIpAddr(request);
        String uri = request.getRequestURI();
        String requestMethod = request.getMethod();
        requestLogDO.setIp(requestIp);
        requestLogDO.setCreateTime(startTime);
        requestLogDO.setSuccess(true);
        requestLogDO.setErrorCode(ResultRO.successCode);
        requestLogDO.setErrorType(ErrorType.success);
        requestLogDO.setErrorMsg(ErrorMsg.successMsg);
        requestLogDO.setInnerMsg(ErrorMsg.successMsg);
        requestLogDO.setRequestMethod(requestMethod);
        requestLogDO.setUri(uri);
//        requestLogDO = RequestLogDOUtil.saveAsync(requestLogDO);
//        RequestLogDOUtil.saveAsync(requestLogDO);
        RequestLogUtil.set(requestLogDO);

//        log.info("[{}({})]", requestLogDO.getRequestMethod(), requestLogDO.getUri());
//        log.info("[requestId:{},{}],[{}({})]",Thread.currentThread().getName(), requestLogDO.getErrorMsg(), requestLogDO.getRequestMethod(), requestLogDO.getUri());

        String ipKey = "ipKey:" + requestIp;

        Object keyCountObj = redisUtil.get(ipKey);
        //如果已经有了赋值
        if (keyCountObj != null) {
            int count = (Integer) keyCountObj;
            //限制1分钟访问50次
            if (count > 3000) {
                //这里只提示未登录
                res.setStatus(ErrorCode.IP_LIMIT_ERROR);
                return false;
            }
            long expireTime = redisUtil.getExpire(ipKey);
            //否则访问次数加1
            count = count + 1;
            redisUtil.set(ipKey, count, expireTime);
        } else {
            redisUtil.set(ipKey, 1, 60);
        }

        if ((requestMethod.equals(RequestMethod.OPTIONS.name())
                || requestMethod.equals(RequestMethod.GET.name())
                || uri.equals("/")
                || uri.contains("test")
                //初始化的请求
                || uri.contains("report/queryReportTypes")
                //查询首页轮播图
                || uri.contains("queryLocation")
                || uri.contains("queryHomeSwipers")
                || uri.contains("getAppLaunchData")
                || uri.contains("tag/queryTags")
                || uri.contains("tag/queryHotTags")
                || uri.contains("tag/queryTagTypes")
                || uri.contains("tag/queryHotTagType")
                || uri.contains("/queryDistricts")
                || uri.contains("/queryHotDistricts")
                || uri.contains("/devUser/queryDevUserDetail")
                || uri.contains("/app/sendErrorLog")
                || uri.contains("/app/getAppConfig")
                //渠道登录的方法
                || uri.contains("queryProvinceDistricts")
                || uri.contains("queryHotProvinceDistricts")
                || uri.contains("queryOtherHomeTypeTalks")
                || uri.contains("readChat")
                || uri.contains("queryChats")
                || uri.contains("refreshKeywords")
                || uri.contains("refreshConfigMap")
                || uri.contains("refreshRedis")
                || uri.contains("qqPayNotify")
                || uri.contains("wxPayNotify")
                || uri.contains("app/checkUpdate")
                || uri.contains("match/queryMatchUsers")
                || uri.contains("authentication")
                || uri.contains("match/queryUsers")
                || uri.contains("talk/queryTalks")
                || uri.contains("message/queryMessages")
                || uri.contains("/webSocketServer")
                || uri.contains("/myHandler")
                || uri.contains("/queryUserTalks")
                || uri.contains("/queryTalkDetail")
                || uri.contains("/queryUserDetail")
                || uri.contains("/queryAppInitData")
                || uri.contains("/queryAppInitDataLoad")
                || uri.contains("/queryAppInitDataLoad1")
                || uri.contains("/queryAppInitDataReady")
                || uri.contains("/img")
                || uri.contains("/css")
                //这里只查询没被封禁的
                //有些接口必须有用户才可访问
                || user != null
                //有些接口必须有开发者账号才可访问
                //或者为这些url，但divId不为null
                || ((uri.contains("user/providerLogin")
                || uri.contains("user/platformLogin")
                || uri.contains("user/miniAppLogin")
                || uri.contains("user/appLogin")
                || uri.contains("appLogin")
                || uri.contains("wxLogin")
                || uri.contains("login")
                || uri.contains("Login")
                || uri.contains("sendAuthCode")
                || uri.contains("oauth")))

        )
                //不允许读取idcard信息
                && !uri.contains("/img/idCard")
        ) {
            return true;
        }
        /*else {
            String origin = request.getHeader("Origin");
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Allow-Methods", "*");
            res.setHeader("Access-Control-Allow-Headers", "Origin,Content-Type,Accept,token,X-Requested-With");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            //这里只提示未登录
            res.setStatus(ErrorCode.NOT_LOGGED_ERROR);
            return false;
        }*/
        if (user == null) {
            throw new SocialNotLoginException();
        } else {
            throw new SocialSystemException("拦截器无法预见异常");
        }
    }
}