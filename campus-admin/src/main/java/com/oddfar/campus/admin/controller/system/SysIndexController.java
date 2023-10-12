package com.oddfar.campus.admin.controller.system;

import com.oddfar.campus.common.domain.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * 首页
 */
@RestController
public class SysIndexController {

    @Value("${campus.version}")
    private String version;

    @Value("${campus.frameworkVersion}")
    private String frameworkVersion;

    /**
     * 版本情况
     */
    @RequestMapping("/version")
    public R version() {
        HashMap<String, String> map = new HashMap<>();
        map.put("version", version);
        map.put("frameworkVersion", frameworkVersion);
        return R.ok(map);
    }


}
