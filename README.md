# Java test app for user managed identity on Azure VMs

## Howto

- Start a new azure vm instance (with at least 2GB of RAM) and after creation attach a user assigned managed identity with Blob Storage permissions
- SSH into the machine and install java `sudo apt install default-jdk`
- Clone the repo and from inside the `test-managed-identities` folder compile the app (`./mvnw package`)
- Run the app with java -jar target/test-managed-identities-SNAPSHOT-with-dependencies.jar <client-id>` where `<client-id>` can be found on the azure portal in the managed identity detail page
- A list of containers should be printed to console
