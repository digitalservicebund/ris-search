# Portal API keys

The portal API can be operated
- without authentication
- with authentication, either using individual OAuth accounts, or API keys

## Structure

The API keys consist of a prefix, which uniquely identifies the key, and a secret.

The keys are stored with the clear-text prefix, and a hash of the secret. Both values can be supplied via the
Spring configuration mechanisms, under `server.api-keys`. They are read by `ConfigurationProperties` class
`de.bund.digitalservice.ris.search.config.security.AuthProperties`.

## Adding a new API key

1. Create a new prefix that identifies the key, e.g. `ris_`.

2. Create a new secure random string to be used as secret.

3. Encode the value by running this Java code:
    ```java
   import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
    
   // ...
   
    Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    String encoded = pbkdf2PasswordEncoder.encode(secret);
    ```

4. Add both values to the configuration's `server.api-keys` list, e.g., in `application.yaml`.

    ```yaml
    server:
      api-keys:
        - prefix: ris_  # from step 1, note the trailing underscore
          hash: ...     # from step 3
   ```

   Note that the alternate syntax may be used if brackets are not supported as keys:

    ```yaml
    server.api-keys.0.prefix: ris_ # from step 1, note the trailing underscore
    server.api-keys.0.hash: ... # from step 3
    ```

5. Concatenate the prefix (step 1) and secret (step 2) to obtain the API key.
   You may test it using e.g. `curl`:
    ```shell
    curl -H "X-Api-Key: ris_..." "http://localhost:8090/v1/legislation?size=1" -v
    ```