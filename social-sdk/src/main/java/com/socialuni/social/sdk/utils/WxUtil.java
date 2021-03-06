package com.socialuni.social.sdk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialuni.social.constant.DateTimeType;
import com.socialuni.social.entity.model.DO.NotifyDO;
import com.socialuni.social.entity.model.DO.user.SocialUserAccountDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.exception.SocialSystemException;
import com.socialuni.social.sdk.constant.platform.PlatformType;
import com.socialuni.social.sdk.constant.platform.UniappProviderType;
import com.socialuni.social.sdk.model.PushMsgDTO;
import com.socialuni.social.sdk.platform.WxErrCode;
import com.socialuni.social.sdk.platform.qq.QQPayResult;
import com.socialuni.social.sdk.platform.weixin.HttpResult;
import com.socialuni.social.sdk.platform.weixin.WxConst;
import com.socialuni.social.sdk.platform.weixin.token.WxTokenResult;
import com.socialuni.social.sdk.repository.user.SocialUserAccountRepository;
import com.socialuni.social.sdk.utils.common.RestUtil;
import com.socialuni.social.utils.UUIDUtil;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author qinkaiyuan
 * @date 2019-10-24 13:30
 */
@Component
@Slf4j
public class WxUtil {
    private static ObjectMapper objectMapper;
    private static SocialUserAccountRepository socialUserAccountRepository;

    @Resource
    public void setObjectMapper(ObjectMapper objectMapper) {
        WxUtil.objectMapper = objectMapper;
    }

    @Resource
    public void setAccountRepository(SocialUserAccountRepository socialUserAccountRepository) {
        WxUtil.socialUserAccountRepository = socialUserAccountRepository;
    }

    private static String wx_mp_id;
    private static String wx_app_id;
    private static String wx_mp_secret;

    @Value("${socialuni.provider.wx.wx-app-id}")
    public void setWx_app_id(String wx_app_id) {
        WxUtil.wx_app_id = wx_app_id;
    }

    @Value("${socialuni.provider.wx.wx-mp-id}")
    public void setWx_mp_id(String wx_mp_id) {
        WxUtil.wx_mp_id = wx_mp_id;
    }

    @Value("${socialuni.provider.wx.wx-mp-secret}")
    public void setWx_mp_secret(String wx_mp_secret) {
        WxUtil.wx_mp_secret = wx_mp_secret;
    }

    /**
     * ????????????token
     *
     * @return
     */
    /*public static String getAccessToken() {
        ResponseEntity<TokenDTO> responseEntity = restTemplate.getForEntity(tokenUrl + "getWxSession", TokenDTO.class);
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }*/

    /**
     * ????????????token
     *
     * @return
     */
    public static String getAccessToken() {
        WxTokenResult wxToken = WxConst.getWxToken();
        if (wxToken.tokenIsValid()) {
            return wxToken.getAccess_token();
        }
        return refreshAccessToken();
    }

    /*public static String refreshAccessToken() {
        ResponseEntity<TokenDTO> responseEntity = restTemplate.getForEntity(tokenUrl + "refreshWxSession", TokenDTO.class);
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }*/


    public static String refreshAccessToken() {
        String appIDAndAppSecret = "appid=" + wx_mp_id + "&secret=" + wx_mp_secret;
        String url = WxConst.wxTokenUrl + appIDAndAppSecret;
        Date curDate = new Date();
        log.info("???????????????????????????:{}", url);
        ResponseEntity<WxTokenResult> responseEntity = RestUtil.restTemplate().getForEntity(url, WxTokenResult.class);
        WxTokenResult wxToken = responseEntity.getBody();
        if (wxToken == null || wxToken.hasError()) {
            log.info("??????????????????????????????:{}", wxToken);
            throw new SocialSystemException("????????????????????????");
        }
        wxToken.setCreateTime(curDate);
        //?????????????????????????????? ?????????5????????????
        wxToken.setExpiresTime(new Date(curDate.getTime() + (long) wxToken.getExpires_in() * DateTimeType.second - 5L * DateTimeType.minute));
        log.info("??????????????????????????????:{}", wxToken);
        WxConst.setWxToken(wxToken);
        return wxToken.getAccess_token();
    }

    /**
     * ????????????????????????
     *
     * @param content
     */
    public static HttpResult checkTextWxSec(String content) {
        HttpResult result = checkContentWxSecPost(content);
        assert result != null;
        if (result.hasError()) {
            if (WxErrCode.token_invalid.equals(result.getErrcode())) {
                WxUtil.refreshAccessToken();
                result = checkContentWxSecPost(content);
            }
        }
        return result;
    }

    private static HttpResult checkContentWxSecPost(String content) {
        HashMap<String, Object> postData = new HashMap<>();
        postData.put("content", content);
        String url = WxConst.wx_msg_sec_check_url + WxUtil.getAccessToken();
        return RestUtil.restTemplate().postForEntity(url, postData, HttpResult.class).getBody();
    }

    public static void checkImgSecPost(String imgUrl) {
        try {
            //?????????????????????
            String[] suffix = imgUrl.split("\\.");
            //?????????.
            String fileTypeName;
            if (suffix.length < 2) {
                fileTypeName = ".jpg";
            } else {
                fileTypeName = "." + suffix[suffix.length - 1];
            }
            File file = File.createTempFile("checkImgTemp", fileTypeName);

            Thumbnails.of(new URL(imgUrl)).size(700, 1300).toFile(file);

            FileSystemResource fileResource  = new FileSystemResource(file);

            // ????????????????????????????????????Map???HashMap???????????????????????????
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("media", fileResource);

            // 2?????????postForEntity????????????
            String imgCheckUrl = WxConst.wx_img_sec_check_url + WxUtil.getAccessToken();

            HttpResult httpResult = RestUtil.getFileRestTemplate().postForEntity(imgCheckUrl, paramMap, HttpResult.class).getBody();
            if (httpResult != null) {
                log.info(httpResult.getErrmsg());
                log.info(httpResult.getErrcode().toString());
            }
        } catch (Exception e) {
            throw new SocialBusinessException("??????????????????");
        }
    }


    public static void wxPushMsgCommon(String openId, String platform, PushMsgDTO pushMsgDTO, NotifyDO notify) {
        String accessToken = WxUtil.getAccessToken();
        pushMsgDTO.setAccess_token(accessToken);
        pushMsgDTO.setTouser(openId);
        //????????????
        String url = WxConst.push_msg_url + accessToken;
        HttpResult result = RestUtil.restTemplate().postForEntity(url, pushMsgDTO, HttpResult.class).getBody();
        PushMessageUtils.savePushMsg(notify, pushMsgDTO, result, platform);
    }

    private static String wx_merchant_id;
    private static String wx_merchant_key;

    @Value("${socialuni.provider.wx.wx-merchant-id}")
    public void setWx_merchant_id(String wx_merchant_id) {
        WxUtil.wx_merchant_id = wx_merchant_id;
    }

    @Value("${socialuni.provider.wx.wx-merchant-key}")
    public void setWx_merchant_key(String wx_merchant_key) {
        WxUtil.wx_merchant_key = wx_merchant_key;
    }

    //????????????
    public static String postPayUrl(String platform, String deviceIp, String orderNo, String total_feeStr, Integer userId) throws IOException {
        SocialUserAccountDO socialUserAccountDO = socialUserAccountRepository.findByProviderAndUserId(UniappProviderType.wx, userId);

        String trade_type = WxConst.mp_pay_trade_type;
        String appId = wx_mp_id;

        Map<String, String> map = new HashMap<>();

        //?????????????????????app
        if (!PlatformType.mp.equals(platform)) {
            trade_type = WxConst.app_pay_trade_type;
            appId = wx_app_id;
        }
        map.put("appid", appId);
        //????????????
        String openId = socialUserAccountDO.getMpOpenId();
        //??????????????????????????????
        if (PlatformType.mp.equals(platform)) {
            map.put("openid", openId);
        }
        String bodystr = "qingchiapp";
        map.put("body", bodystr);
        map.put("mch_id", wx_merchant_id);
        String nonce_strstr = UUIDUtil.getUUID();
        map.put("nonce_str", nonce_strstr);
        map.put("notify_url", WxConst.wx_pay_result_notify_url);

        map.put("out_trade_no", orderNo);
        map.put("spbill_create_ip", deviceIp);
        //10???
        map.put("total_fee", total_feeStr);
        map.put("trade_type", trade_type);

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.setContentType(MediaType.APPLICATION_XML);
        StringBuilder xmlString = new StringBuilder();

        String appIdStr = "<appid>" + appId + "</appid>";
        String openIdXml = "";
        if (PlatformType.mp.equals(platform)) {
            openIdXml = "<openid>" + openId + "</openid>";
        }
        String body = "<body>" + bodystr + "</body>";
        String mch_id = "<mch_id>" + wx_merchant_id + "</mch_id>";
        String nonce_str = "<nonce_str>" + nonce_strstr + "</nonce_str>";

        String sign = getSignToken(map);
        String signStr = "<sign>" + sign + "</sign>";

        String notify = "<notify_url>" + WxConst.wx_pay_result_notify_url + "</notify_url>";
        String out_trade_no_xml = "<out_trade_no>" + orderNo + "</out_trade_no>";
        String spbill_create_ip = "<spbill_create_ip>" + deviceIp + "</spbill_create_ip>";
        String total_fee = "<total_fee>" + total_feeStr + "</total_fee>";
        String trade_typeStr = "<trade_type>" + trade_type + "</trade_type>";

        xmlString.append("<xml>")
                .append(appIdStr);
        //?????????????????????openid
        if (PlatformType.mp.equals(platform)) {
            xmlString.append(openIdXml);
        }
        xmlString.append(body)
                .append(mch_id)
                .append(nonce_str)
                .append(notify)
                .append(out_trade_no_xml)
                .append(spbill_create_ip)
                .append(total_fee)
                .append(trade_typeStr)
                .append(signStr)
                .append("</xml>");

        // ?????? HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlString.toString(), requestHeader);
        ResponseEntity<String> responseEntity = RestUtil.restTemplate().postForEntity(WxConst.wx_pay_url, requestEntity, String.class);
        String xmlStr = responseEntity.getBody();
        XStream xstream = new XStream();
        xstream.alias("xml", QQPayResult.class);
        Object qqPayResult = xstream.fromXML(xmlStr);
        String result = objectMapper.writeValueAsString(qqPayResult);
        log.info(result);
        QQPayResult result1 = objectMapper.readValue(result, QQPayResult.class);

        return result1.getPrepay_id();
    }

    //????????????
    /*public static String getFrontPaySign(Map<String, String> map) {
        map.put("appId", WxConst.mp_app_id);
        map.put("signType", "MD5");
        return getSignToken(map);
    }*/

    public static String getFrontPaySign(String platform, String prepayid, String packageStr, String nonceStr, String timeStamp) {
        Map<String, String> map = new HashMap<>();
        if (PlatformType.mp.equals(platform)) {
            map.put("appId", wx_mp_id);
            map.put("signType", "MD5");
            map.put("timeStamp", timeStamp);
            map.put("nonceStr", nonceStr);
        } else {
            map.put("appid", wx_app_id);
            map.put("partnerid", wx_merchant_id);
            map.put("prepayid", prepayid);
            map.put("noncestr", nonceStr);
            map.put("timestamp", timeStamp);
        }
        map.put("package", packageStr);
        return getSignToken(map);
    }

    /**
     * ????????????
     *
     * @param map
     * @return
     */
    public static String getSignToken(Map<String, String> map) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
        // ??????????????????????????????????????? ASCII ????????????????????????????????????
        infoIds.sort(Map.Entry.comparingByKey());
        // ??????????????????????????????
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> item : infoIds) {
            String itemKey = item.getKey();
            String itemVal = item.getValue();
            if (StringUtils.isNotEmpty(itemKey) && StringUtils.isNotEmpty(itemVal)) {
                sb.append(itemKey).append("=").append(itemVal).append("&");
            }
        }
        //key?????????
        String result = sb.toString() + "key=" + wx_merchant_key;
        //??????MD5??????
        result = DigestUtils.md5Hex(result).toUpperCase();
        return result;
    }
}
