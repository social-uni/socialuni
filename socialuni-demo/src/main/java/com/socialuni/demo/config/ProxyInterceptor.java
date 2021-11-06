package com.socialuni.demo.config;

import com.socialuni.api.config.SocialFeignHeaderName;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.entity.model.DO.RequestLogDO;
import com.socialuni.social.entity.model.DO.user.SocialUserAccountDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.exception.SocialNotLoginException;
import com.socialuni.social.exception.SocialSystemException;
import com.socialuni.social.exception.constant.ErrorType;
import com.socialuni.social.sdk.constant.ErrorMsg;
import com.socialuni.social.sdk.utils.RedisUtil;
import com.socialuni.social.sdk.utils.RequestLogUtil;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import com.socialuni.social.sdk.utils.model.SocialUserAccountUtil;
import com.socialuni.social.web.sdk.utils.IpUtil;
import com.socialuni.social.web.sdk.utils.RequestIdUtil;
import com.socialuni.social.web.sdk.utils.SocialTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Component
@Slf4j
public class ProxyInterceptor implements HandlerInterceptor {

    @Value("${socialuni.server-url:https://api.socialuni.cn}")
    private String url;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RestTemplate restTemplate;
    @Value("${socialuni.secret-key}")
    private String socialDevSecretKey;
    /*
     * 进入controller层之前拦截请求
     * 在请求处理之前进行调用（Controller方法调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse res, Object o) {
        // 代理查询的内容。
        if(!this.isCentralRecord(request)){
            ResponseEntity route = this.route(request);
            Object body = route.getBody();
            PrintWriter writer = null;
            try {
                res.setHeader("Content-type", "application/json;charset=UTF-8");
                writer = res.getWriter();
                writer.println(body);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 没有异常..
        if( ex == null  && isCentralRecord(request)){
            // 往中心添加数据。
            this.route(request);
        }
    }


    /**
     * 是否是联盟中心记录,该数据.
     * @param request
     * @return
     */
    public boolean isCentralRecord(HttpServletRequest request){
        String uri = request.getRequestURI();
        // 里面是需要往中心添加数据的.
        return uri.equals("/");
    }




    public ResponseEntity route(HttpServletRequest request)  {

        try {
            String characterEncoding = request.getCharacterEncoding();
            String body = IOUtils.toString(request.getInputStream(), characterEncoding);
            // set head.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(SocialFeignHeaderName.socialSecretKeyHeaderName, socialDevSecretKey);
            //如果有token，或者社交登录时，不携带token，手动设置token
            if (SocialTokenUtil.hasToken()) {
                //走了这里一定是绑定了社交联盟账号，获取社交联盟账户
                SocialUserAccountDO socialUserAccountDO = SocialUserAccountUtil.getMineSocialAccount();
                if (socialUserAccountDO != null) {
                    String uniToken = socialUserAccountDO.getSessionKey();
                    headers.set(SocialFeignHeaderName.socialTokenHeaderName, uniToken);
                } else {
                    String socialuniToken = SocialTokenUtil.getSocialuniToken();
                    if (socialuniToken != null) {
                        headers.set(SocialFeignHeaderName.socialTokenHeaderName, socialuniToken);
                    }
                }
            } else {
                String socialuniToken = SocialTokenUtil.getSocialuniToken();
                if (socialuniToken != null) {
                    headers.set(SocialFeignHeaderName.socialTokenHeaderName, socialuniToken);
                }
            }
            //
            HttpEntity<String> requestEntity = new HttpEntity<>(body,headers);



            // run
            String url = this.url + request.getRequestURI();
            ResponseEntity<Object> exchange = restTemplate.exchange(url,
                    HttpMethod.valueOf(request.getMethod()),
                    requestEntity,
                    Object.class,
                    request.getParameterMap());
            return exchange;
        } catch (final HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode());
        }catch ( IOException e) {
            log.error("error",e);
//            return new ResponseEntity<>("{}",HttpStatus.INTERNAL_SERVER_ERROR);
            throw new RuntimeException("proxy IO ",e);
        }
    }
}
