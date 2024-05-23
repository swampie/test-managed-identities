package io.seqera.azure;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void testValidConfigParse() throws Exception{

        var config = Config.loadConfig(Paths.get(this.getClass().getResource("test.json").toURI()).toAbsolutePath().toString());
        assertNotNull(config);
        assertEquals("managedIdentityId", config.getManagedIdentityId());
        assertEquals("subscriptionId", config.getSubscriptionId());
        assertEquals("batch name", config.getBatchAccountName());

    }

    @Test
    public void testInValidConfigParse() throws Exception{

        var config = Config.loadConfig("unexistent.file");
        assertNull(config);


    }


}
