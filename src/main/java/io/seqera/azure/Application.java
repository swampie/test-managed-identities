package io.seqera.azure;

import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerItem;

import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        String managedIdentity = args[0];
        if(managedIdentity == null){
            System.exit(-1);
        }
        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder()
                .managedIdentityClientId(managedIdentity)
                .build();

        BlobServiceClient client = new BlobServiceClientBuilder()
                .credential(defaultCredential)
                .endpoint("https://seqeralabs.blob.core.windows.net")
                .buildClient();

        PagedIterable<BlobContainerItem> blobContainerItems = client.listBlobContainers();
        Set<String> collect = blobContainerItems.stream().map(it -> it.getName()).collect(Collectors.toSet());
        collect.forEach(System.out::println);
    }
}
