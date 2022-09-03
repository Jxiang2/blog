package com.jxiang.blog.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class QiniuUtils {

    @Autowired
    Environment environment;

    public Map<String, Object> upload(MultipartFile file, String fileName) {

        Configuration cfg = new Configuration(Region.regionNa0());
        UploadManager uploadManager = new UploadManager(cfg);
        String bucket = environment.getProperty("credentials.qiniu.bucket");
        try {
            byte[] uploadBytes = file.getBytes();
            Auth auth = Auth.create(
                environment.getProperty("credentials.qiniu.accessKey"),
                Objects.requireNonNull(environment.getProperty("credentials.qiniu.accessSecretKey"))
            );
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(uploadBytes, fileName, upToken);
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            Map<String, Object> result = new HashMap();
            result.put("result", true);
            result.put("url", environment.getProperty("credentials.qiniu.url"));
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, Object> result = new HashMap();
        result.put("result", false);
        result.put("url", environment.getProperty("credentials.qiniu.url"));
        return result;
    }

}