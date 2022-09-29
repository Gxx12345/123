package com.alibaba.reggie.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Getter;

/**
 * 短信发送工具类
 */
@Getter
public class SMSUtils {

    private static final String SIGN = "阿里云短信测试";
    private static final String TEMPLATE_CODE = "SMS_154950909";

    public static void main(String[] args) {
        sendMessage(SIGN,
                TEMPLATE_CODE,
                "15515153267",
                String.valueOf(ValidateCodeUtils.generateValidateCode(4)));
    }

    /**
     * 发送短信
     *
     * @param signName     签名
     * @param templateCode 模板
     * @param phoneNumbers 手机号
     * @param param        参数
     */
    public static void sendMessage(String signName, String templateCode, String phoneNumbers, String param) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                "LTAI5t5pLbCNzY6gEqHKQAeB",
                "gSCtDWvjU7XHNYAQbGllUxDJZ2724q");
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + param + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
