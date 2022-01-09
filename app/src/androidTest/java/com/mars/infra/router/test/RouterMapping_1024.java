package com.mars.infra.router.test;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
public class RouterMapping_1024 {
    public static Map get() {
        Map<String, String> map = new HashMap();
        map.put("/main/page", "com.mars.infra.router.MainActivity");
        map.put("/login", "com.mars.infra.router.LoginActivity");
        return map;
    }
}