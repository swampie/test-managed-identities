package io.seqera.azure;

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

import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        String managedIdentity = args[0];
        String tenantId = args[1];
        String subscriptionId = args[2];
        if(managedIdentity == null){
            System.exit(-1);
        }
        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder()
                .managedIdentityClientId(managedIdentity)
                .build();

        loadBuckets(defaultCredential);
        loadPools(defaultCredential, tenantId, subscriptionId);
    }

    private static void loadPools(DefaultAzureCredential defaultCredential,String tenantId, String subscriptionId){
        AzureProfile profile = new AzureProfile(tenantId,subscriptionId, AzureEnvironment.AZURE);    // Assume Global Cloud is used
        BatchManager batchManager = BatchManager
                .authenticate(defaultCredential, profile);
        Set<String> pools = batchManager.pools().listByBatchAccount("seqera_rg", "seqeralabs")
                .stream().map(it -> it.name()).collect(Collectors.toSet());
        pools.forEach(System.out::println);

    }

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
