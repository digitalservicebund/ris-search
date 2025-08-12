package de.bund.digitalservice.ris.search.integration.config;

import org.opensearch.testcontainers.OpensearchContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.SelinuxContext;

@SuppressWarnings("rawtypes")
public class CustomOpensearchContainer extends OpensearchContainer {

  private static final String DOCKER_IMAGE_OPEN_SEARCH = "opensearchproject/opensearch:2.0.0";
  private static final String CLUSTER_NAME = "cluster.name";
  private static final String OPEN_SEARCH = "opensearch";
  private static final String DISCOVERY_TYPE = "discovery.type";
  private static final String DISCOVERY_TYPE_SINGLE_NODE = "single-node";
  private static final String DISABLE_SECURITY_PLUGIN = "DISABLE_SECURITY_PLUGIN";
  private static final int HOST_PORT = 9300;
  private static final int CONTAINER_PORT = 9200;

  public CustomOpensearchContainer() {
    super(DOCKER_IMAGE_OPEN_SEARCH);
    addFixedExposedPort(HOST_PORT, CONTAINER_PORT);
    addEnv(DISCOVERY_TYPE, DISCOVERY_TYPE_SINGLE_NODE);
    addEnv(DISABLE_SECURITY_PLUGIN, Boolean.TRUE.toString());
    addEnv(CLUSTER_NAME, OPEN_SEARCH);

    withClasspathResourceMapping(
            "openSearch/mounted/",
            "/usr/share/opensearch/config/mounted/",
            BindMode.READ_ONLY,
            SelinuxContext.NONE)
        .close();
  }
}
