# Container image

Container images running the application are automatically published by the pipeline to
the [GitHub Packages Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry).

**To run the latest published image:**

```bash
docker run -p8080:8080 "ghcr.io/digitalservicebund/ris-search:$(git log -1 origin/main --format='%H')"
```

The service will be accessible at `http://localhost:8080`.

Container images are built using the multi-stage `backend/Dockerfile.prod`, which runs `./gradlew build` and then
copies the resulting jar into a minimal runtime image:

```bash
docker build -f backend/Dockerfile.prod -t ghcr.io/digitalservicebund/ris-search backend
docker run -p8080:8080 ghcr.io/digitalservicebund/ris-search
```

Container images in the registry are [signed with keyless signatures](https://github.com/sigstore/cosign/blob/main/KEYLESS.md).

**To verify an image**:

```bash
COSIGN_EXPERIMENTAL=1 cosign verify "ghcr.io/digitalservicebund/ris-search:$(git log -1 origin/main --format='%H')"
```

If you need to push a new container image to the registry manually, use Docker:

```bash
echo [github-token] | docker login ghcr.io -u [github-user] --password-stdin
docker build -f backend/Dockerfile.prod -t "ghcr.io/digitalservicebund/ris-search:$(git log -1 --format='%H')" backend
docker push "ghcr.io/digitalservicebund/ris-search:$(git log -1 --format='%H')"
```

**Note:** Make sure you're using a GitHub token with the necessary `write:packages` scope for this to work.
