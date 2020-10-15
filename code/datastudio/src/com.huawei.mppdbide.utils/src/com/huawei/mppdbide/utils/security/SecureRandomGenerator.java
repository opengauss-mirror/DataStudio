/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.security;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecureRandomGenerator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author c00550043
 * @version [DataStudio 1.0.0, 5 Aug, 2020]
 * @since 5 Aug, 2020
 */
public class SecureRandomGenerator {
    private static final AtomicInteger CNT_VAL = new AtomicInteger(0);

    private static void setSeed(SecureRandom random) {
        if (CNT_VAL.incrementAndGet() > 100) {
            CNT_VAL.set(0);
            byte[] seed = new byte[64];
            SecureRandom seedRnd = new SecureRandom();
            seedRnd.nextBytes(seed);
            random.setSeed(seed);
        }
    }

    /**
     * Get random number
     *
     * @return the byte [ ]
     */
    public static byte[] getRandomNumber() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        setSeed(random);
        random.nextBytes(iv);
        return iv;
    }
}
