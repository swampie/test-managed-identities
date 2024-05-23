package io.seqera.azure;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.batch.BatchManager;
import com.azure.resourcemanager.batch.implementation.PoolsImpl;
import com.azure.resourcemanager.batch.models.Pools;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.auth.BatchApplicationTokenCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        Config config = Config.loadConfig(args[0]);
        if(config == null){
            log.error("Unable to parse config {}",args[0]);
        }
        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder()
                .managedIdentityClientId(config.getManagedIdentityId())
                .build();

        loadBuckets(defaultCredential);
        loadPools(config);
    }


    private static void loadPools(Config config) {
        log.debug("[AZURE BATCH] Creating Azure Batch client using Managed Identity credentials");

        final String batchEndpoint = "https://batch.core.windows.net/";
        final String authenticationEndpoint = "https://management.core.windows.net/";

        final DefaultAzureCredentialBuilder credentialBuilder = new DefaultAzureCredentialBuilder();
        if( config.getManagedIdentityId() != null) {
            log.debug("[AZURE BATCH] Client ID: ${clientId}");
            credentialBuilder.managedIdentityClientId(config.getManagedIdentityId());
        }
        DefaultAzureCredential credential = credentialBuilder.build();
        final TokenRequestContext tokenContext = new TokenRequestContext()
                .setTenantId(config.getTenantId())
                .addScopes(String.format("%s/.default", AzureEnvironment.AZURE.getManagementEndpoint()));
        log.info("[AZURE BATCH] Tenant ID: ${tokenContext.getTenantId()}");
        AccessToken token = credential.getTokenSync(tokenContext);

        BatchApplicationTokenCredentials batchApplicationTokenCredentials = new BatchApplicationTokenCredentials(
                config.getBatchEndpointUrl(), // base URL
                config.getManagedIdentityId(),           // client ID
                null,
                config.getTenantId(), // domain (tenant?)
                batchEndpoint, // batchEndpoint
                authenticationEndpoint // authenticationEndpoint
        );
        BatchClient client = BatchClient.open(batchApplicationTokenCredentials);
        try {
            client.poolOperations().listPools().stream().forEach(it -> System.out.println(it.displayName()));
        } catch (IOException e) {
            log.error("Unable to execute list pools", e);
        }
    }

    /*private static void loadPools(DefaultAzureCredential defaultCredential,String tenantId, String subscriptionId){
        AzureProfile profile = new AzureProfile(tenantId,subscriptionId, AzureEnvironment.AZURE);    // Assume Global Cloud is used
        BatchManager batchManager = BatchManager
                .authenticate(defaultCredential, profile);
        Set<String> pools = batchManager.pools().listByBatchAccount("seqera_rg", "seqeralabs")
                .stream().map(it -> it.name()).collect(Collectors.toSet());
        pools.forEach(System.out::println);

    }*/

    private static void loadBuckets(DefaultAzureCredential defaultCredential) {
        BlobServiceClient client = new BlobServiceClientBuilder()
                .credential(defaultCredential)
                .endpoint("https://seqeralabs.blob.core.windows.net")
                .buildClient();

        PagedIterable<BlobContainerItem> blobContainerItems = client.listBlobContainers();
        Set<String> collect = blobContainerItems.stream().map(it -> it.getName()).collect(Collectors.toSet());
        collect.forEach(System.out::println);
    }
}
