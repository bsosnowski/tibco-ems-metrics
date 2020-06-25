/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems.model;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.StateInfo;

public class TibcoEmsMetrics {

  private StateInfo stateInfo;
  private ServerInfo serverInfo;
  private DestinationInfo[] queues;
  private DestinationInfo[] topics;
  private String serverLatency;

  public StateInfo getStateInfo() {
    return stateInfo;
  }

  public void setStateInfo(StateInfo stateInfo) {
    this.stateInfo = stateInfo;
  }

  public ServerInfo getServerInfo() {
    return serverInfo;
  }

  public void setServerInfo(ServerInfo serverInfo) {
    this.serverInfo = serverInfo;
  }

  public DestinationInfo[] getQueues() {
    return queues;
  }

  public void setQueues(DestinationInfo[] queues) {
    this.queues = queues;
  }

  public DestinationInfo[] getTopics() {
    return topics;
  }

  public void setTopics(DestinationInfo[] topics) {
    this.topics = topics;
  }

  public String getServerLatency() {
    return serverLatency;
  }

  public void setServerLatency(String serverLatency) {
    this.serverLatency = serverLatency;
  }
    
}