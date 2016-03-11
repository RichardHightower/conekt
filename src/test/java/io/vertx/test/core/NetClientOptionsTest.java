package io.vertx.test.core;

import io.smallvertx.core.net.NetClientOptions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test that the default object of NetClientOptions equals to when creating
 * a NetClientOptions object from an empty Json object. Previously the json constructor
 * used null for the enabledCipherSuite property which breaks the addEnabledCipherSuite
 * operation.
 */

/**
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 */
public class NetClientOptionsTest {

    @Test
    public final void testEquals() {
        NetClientOptions options1 = new NetClientOptions();
        NetClientOptions options2 = new NetClientOptions();
        assertEquals(options1, options2);
    }

    @Test
    public final void testAdd() {
        NetClientOptions options = new NetClientOptions();
        options.addEnabledCipherSuite("XXX");
    }

}
