package com.mars.infra.router.test;

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
public class TestUriAnnotationInit_5023 implements IUriAnnotationInit {
    public void init(TestUriHandler handler) {
        handler.register("main", "/main/page", "com.mars.infra.router.MainActivity");
        handler.register("login", "/login", "com.mars.infra.router.LoginActivity");
    }
}