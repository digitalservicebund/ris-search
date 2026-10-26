package de.bund.digitalservice.ris.search.config.obs;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/** S3Mock container providing an S3 compatible API for integration testing. */
public class CustomS3MockContainer extends GenericContainer<CustomS3MockContainer> {

  private static final String DOCKER_IMAGE_S3_MOCK = "adobe/s3mock:5.2.3";
  private static final int HTTP_PORT = 9090;

  /** Constructor for CustomS3MockContainer. Exposes the HTTP port and waits until it responds. */
  public CustomS3MockContainer() {
    super(DockerImageName.parse(DOCKER_IMAGE_S3_MOCK));
    addExposedPort(HTTP_PORT);
    addEnv(
        "COM_ADOBE_TESTING_S3MOCK_STORE_INITIAL_BUCKETS",
        "caselaw,literature,administrative-directive,norm,portal,public");
    setWaitStrategy(Wait.forHttp("/").forPort(HTTP_PORT).forStatusCode(200));
  }

  public String getHttpEndpoint() {
    return "http://" + getHost() + ":" + getMappedPort(HTTP_PORT);
  }
}
