/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
  private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

  private static final Config INSTANCE = new Config();
  
  private volatile String emsConnectionAdminUrl;
  private volatile String emsConnectionAdminUser;
  private volatile String emsConnectionAdminPassword;
  private volatile int emsConnectionRetryCount;
  private volatile int emsConnectionReteryIntervalSeconds;

  private volatile String graphiteConnectionHost;
  private volatile int graphiteConnectionPort;
  private volatile int graphiteConnectionRetryCount;
  private volatile int graphiteConnectionRetryIntervalSeconds;

  private volatile String metricsPrefix;
  private volatile long metricsIntervalMillis;
  private volatile Boolean metricsEnabledDestinations;

  /**
   * Default constructor for the Config.
   */
  public Config() {
    Properties properties = new Properties();
    try {
      InputStream input = new FileInputStream("metrics.properties");
      properties.load(input);
    } catch (FileNotFoundException e) {
      LOGGER.log(Level.SEVERE, "Properties file not found", e);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error reading properties file", e);
    }
    emsConnectionAdminUrl = properties.getProperty("ems.connection.admin.url");
    emsConnectionAdminUser = properties.getProperty("ems.connection.admin.user");
    emsConnectionAdminPassword = properties.getProperty("ems.connection.admin.password");
    emsConnectionRetryCount = Integer.parseInt(
      properties.getProperty("ems.connection.retry.count"));
    emsConnectionReteryIntervalSeconds = Integer.parseInt(
      properties.getProperty("ems.conenction.retry.interval_seconds"));
  
    graphiteConnectionHost = properties.getProperty("graphite.connection.host");
    graphiteConnectionPort = Integer.parseInt(properties.getProperty("graphite.connection.port"));
    graphiteConnectionRetryCount = Integer.parseInt(
      properties.getProperty("graphite.connection.retry.count"));
    graphiteConnectionRetryIntervalSeconds = Integer.parseInt(
      properties.getProperty("graphite.connection.retry.interval_seconds"));

    metricsPrefix = properties.getProperty("metrics.prefix");
    metricsEnabledDestinations = Boolean.parseBoolean(
      properties.getProperty("metrics.enabled.destinations")
      );
    metricsIntervalMillis = Integer.parseInt(
      properties.getProperty("metrics.interval_millis")
      );

  }

  public static Config getInstance() {
    return INSTANCE;
  }

  public String getEmsConnectionAdminUrl() {
    return emsConnectionAdminUrl;
  }

  public String getEmsConnectionAdminUser() {
    return emsConnectionAdminUser;
  }

  public String getEmsConnectionAdminPassword() {
    return emsConnectionAdminPassword;
  }

  public String getGraphiteConnectionHost() {
    return graphiteConnectionHost;
  }

  public int getGraphiteConnectionPort() {
    return graphiteConnectionPort;
  }

  public String getMetricsPrefix() {
    return metricsPrefix;
  }

  public long getMetricsIntervalMillis() {
    return metricsIntervalMillis;
  }

  public Boolean getMetricsEnabledDestinations() {
    return metricsEnabledDestinations;
  }

  public int getEmsConnectionRetryCount() {
    return emsConnectionRetryCount;
  }
  
  public int getEmsConnectionReteryIntervalSeconds() {
    return emsConnectionReteryIntervalSeconds;
  }
  
  public int getGraphiteConnectionRetryCount() {
    return graphiteConnectionRetryCount;
  }
  
  public int getGraphiteConnectionRetryIntervalSeconds() {
    return graphiteConnectionRetryIntervalSeconds;
  }
    
}