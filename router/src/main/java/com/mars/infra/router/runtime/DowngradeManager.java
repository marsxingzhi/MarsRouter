package com.mars.infra.router.runtime;

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
public class DowngradeManager {

    // ASM
    public static <T> T getDowngradeImpl(Class<T> serviceClass) {
        return null;
    }

    // ASM
    public static boolean isForceDowngrade() {
        return false;
    }
}
