/**
 * 
 */
package com.huawei.mppdbide.common;

import java.util.Optional;

/**
 * @author z00588921
 *
 */
public interface IConnectionProvider {
    Optional<IConnection> getFreeConnection();
}
