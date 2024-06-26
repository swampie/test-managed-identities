package io.seqera.azure;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Config {

    //serialization
    public Config(){}

    private String managedIdentityId;
    private String subscriptionId;
    private String tenantId;
    private String batchEndpointUrl;
    private String resourceGroup;
    private String batchAccountName;

    public String getManagedIdentityId() {
        return managedIdentityId;
    }

    public void setManagedIdentityId(String managedIdentityId) {
        this.managedIdentityId = managedIdentityId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getBatchEndpointUrl() {
        return batchEndpointUrl;
    }

    public void setBatchEndpointUrl(String batchEndpointUrl) {
        this.batchEndpointUrl = batchEndpointUrl;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    public String getBatchAccountName() {
        return batchAccountName;
    }

    public void setBatchAccountName(String batchAccountName) {
        this.batchAccountName = batchAccountName;
    }

    protected static Config loadConfig(String configurationPath) {
        ObjectMapper om = new ObjectMapper();
        try {
            File file = Paths.get(configurationPath).toFile();
            return om.readValue(new FileReader(file), Config.class);
        } catch (IOException e) {
            return null;
        }

    }
}
