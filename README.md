# Java test app for user managed identity on Azure VMs

## Howto

- Start a new azure vm instance (with at least 2GB of RAM) and after creation attach a user assigned managed identity with Blob Storage permissions
- SSH into the machine and install java `sudo apt install default-jdk`
- Clone the repo and from inside the `test-managed-identities` folder compile the app (`./mvnw package`)
- Create a config.json file with the following data
```json
{
    "managedIdentityId":"<client_id>",
    "tenantId":"<tenant_id>",
    "subscriptionId":"<subscription_id>",
    "resourceGroup":"<rg_containing_batch>",
    "batchAccountName":"<batch_account_name>",
    "batchEndpointUrl":"<batch_endoint_url>"
}
```
- Run the app with `AZURE_LOG_LEVEL=VERBOSE java --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED -jar target/test-managed-identity-1.0-SNAPSHOT-jar-with-dependencies.jar <config_json_path>` where `<config_json_path>` points to the full path of the json built above
- A list of containers should be printed to console
- A list of pool should be printed to console (NOT WORKING)
