package net.bons.comptes.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
  public int getPort() {
    String port = System.getenv("PORT");
    if (port == null) {
      port = System.getProperty("PORT", "5000");
    }

    LOG.info("Listen on {}", port);
    return Integer.valueOf(port);
  }
}
