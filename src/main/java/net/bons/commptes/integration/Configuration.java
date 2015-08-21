package net.bons.commptes.integration;

public class Configuration {
  public int getPort() {
    String port = System.getenv("PORT");
    if (port == null) {
      port = System.getProperty("PORT", "5000");
    }
    return Integer.valueOf(port);
  }
}
