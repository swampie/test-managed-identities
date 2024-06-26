package io.seqera.azure;

import com.azure.compute.batch.BatchClientBuilder;
import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception{

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

        final DefaultAzureCredentialBuilder credentialBuilder = new DefaultAzureCredentialBuilder();
        if( config.getManagedIdentityId() != null) {
            log.debug("[AZURE BATCH] Client ID: ${clientId}");
            credentialBuilder.managedIdentityClientId(config.getManagedIdentityId());
        }

        var client = new BatchClientBuilder()
                    .credential(new ManagedIdentityCredentialBuilder().clientId(config.getManagedIdentityId()).build())
                .endpoint(config.getBatchEndpointUrl())
                .buildClient();
        client.listPools().stream().forEach(it -> System.out.println(it.getId()));

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
