package com.mars.infra.router;

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
public class ServiceManager {


    public int test(int value) {
        if (value == 1) {
            return 1;
        } else if (value == 2) {
            return 2;
        }
        return -1;
    }

    // ASM
    public static <T> T getService(Class<T> serviceClass) {
        if (serviceClass.getName().equals("com.mars.infra.router.ILoginService")) {
            T loginService = (T) new LoginServiceImpl();
            return loginService;
        }
//        else if (serviceClass.getName().equals("com.mars.infra.router.IFakeService")) {
//            T fakeService = (T) new FakeServiceImpl();
//            return fakeService;
//        }
        return null;
    }
}
