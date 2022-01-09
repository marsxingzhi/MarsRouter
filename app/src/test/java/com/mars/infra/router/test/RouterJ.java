package com.mars.infra.router.test;

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
public class RouterJ {

    private static boolean isInit = false;
    private static final TestUriHandler2 uriHandler = new TestUriHandler2();

    public static void loadRouterMap() {
        new TestUriAnnotationInit_111().init(uriHandler);
        isInit = true;
    }

    public static class TestUriAnnotationInit_111 {
        public void init(TestUriHandler2 handler) {
            handler.register("main", "/main/page", "com.mars.infra.router.MainActivity");
            handler.register("login", "/login", "com.mars.infra.router.LoginActivity");
        }
    }

    public static class TestUriHandler2 {

        public void register(String module, String path, String className) {

        }
    }
}
