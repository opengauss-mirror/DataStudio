/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.utils.security;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecureRandomGenerator.
 *
 * @since 3.0.0
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
