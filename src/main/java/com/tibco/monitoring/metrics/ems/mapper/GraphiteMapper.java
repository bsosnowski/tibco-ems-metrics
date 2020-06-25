/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems.mapper;

import com.tibco.monitoring.metrics.ems.Config;
import com.tibco.monitoring.metrics.ems.Utils;
import com.tibco.monitoring.metrics.ems.model.TibcoEmsMetrics;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.StateInfo;
import com.tibco.tibjms.admin.TopicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphiteMapper {

  static String ns = ".";
  static String ms = " ";
  static String eol = "\n";
  static String prefix = Config.getInstance().getMetricsPrefix();

  private GraphiteMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Maps data between TibcoEmsMetrics to List.
   * @param metrics represented as TibcoEmsMetrics
   * @param timestamp timestamp of metrics collection
   * @return
   */
  public static List<String> map(TibcoEmsMetrics metrics, long timestamp) {

    HashMap<String, String> map = new HashMap<>();
    mapStateInfo(metrics.getStateInfo(), map);
    mapServerInfo(metrics.getServerInfo(), map);
    
    DestinationInfo[] queues = metrics.getQueues();
    if (queues != null) {
      mapQueues(metrics.getQueues(), map);
    }
    DestinationInfo[] topics = metrics.getTopics();
    if (topics != null) {
      mapTopics(metrics.getTopics(), map);
    }   

    mapServerLatency(metrics.getServerLatency(), map);
    ArrayList<String> metricList = new ArrayList<>();
    map.forEach((k,v) ->
         metricList.add(prefix + ns + k + ms + v + ms + String.valueOf(timestamp) + eol)
    );
    return metricList;

  }

  private static void mapServerLatency(String sl, HashMap<String, String> map) {
    String conPfx = "connections" + ns;
    map.put(conPfx + "server_response_time_ms",sl);
  }

  /**
   * Dictionary of EMS states. SERVER_STATE_STANDBY = 3; SERVER_STATE_ACTIVE = 4;
   * SERVER_COMPSTATE_WAIT_FOR_PEER = 8; SERVER_COMPSTATE_SYNCHRONIZING = 16;
   * SERVER_COMPSTATE_REPLICATING = 32; SERVER_COMPSTATE_PAUSED = 48;
   * SERVER_COMPSTATE_STANDALONE = 64; SERVER_STATE_WAIT_FOR_PEER = 8;
   * SERVER_STATE_STANDBY_SYNCHRONIZING = 19; SERVER_STATE_STANDBY_REPLICATING =
   * 35; SERVER_STATE_STANDBY_PAUSED = 51; SERVER_STATE_ACTIVE_SYNCHRONIZING = 20;
   * SERVER_STATE_ACTIVE_REPLICATING = 36; SERVER_STATE_ACTIVE_PAUSED = 52;
   * SERVER_STATE_ACTIVE_STANDALONE = 68;
   */
  private static void mapStateInfo(StateInfo si, HashMap<String, String> map) {
    map.put("state", String.valueOf(si.getState().get()));
  }

  private static void mapServerInfo(ServerInfo si, HashMap<String, String> map) {
    // Connections info
    String conPfx = "connections" + ns;
    map.put(conPfx + "admin_connection_count", String.valueOf(si.getAdminConnectionCount()));
    map.put(conPfx + "client_connection_count", String.valueOf(si.getClientConnectionCount()));
    map.put(conPfx + "connection_count", String.valueOf(si.getConnectionCount()));
    map.put(conPfx + "max_connections", String.valueOf(si.getMaxConnections()));
    // Server info
    String srvPfx = "server" + ns;
    map.put(srvPfx + "consumer_count", String.valueOf(si.getConsumerCount()));
    map.put(srvPfx + "producer_count", String.valueOf(si.getProducerCount()));
    map.put(srvPfx + "durable_count", String.valueOf(si.getDurableCount()));
    map.put(srvPfx + "pending_msg_count", String.valueOf(si.getPendingMessageCount()));
    map.put(srvPfx + "pending_msg_size", String.valueOf(si.getPendingMessageSize()));
    // Bytes
    map.put(srvPfx + "msg_mem", String.valueOf(si.getMsgMem()));
    // Bytes 0 - no limit
    map.put(srvPfx + "msg_mem_max", String.valueOf(si.getMaxMsgMemory()));
    // Bytes
    map.put(srvPfx + "msg_mem_pooled", String.valueOf(si.getMsgMemPooled()));
    // 0 if not enabled
    map.put(srvPfx + "msg_pool_size", String.valueOf(si.getMessagePoolSize()));
    // Bytes 0 if not enabled
    // https://support.tibco.com/s/article/Tibco-KnowledgeArticle-Article-34821
    map.put(srvPfx + "reserve_memory", String.valueOf(si.getReserveMemory()));
    map.put(srvPfx + "uptime", String.valueOf(si.getUpTime())); // Millis
    map.put(srvPfx + "topic_count", String.valueOf(si.getTopicCount()));
    map.put(srvPfx + "queue_count", String.valueOf(si.getQueueCount()));
    // Disk statistics
    String stoPfx = "store" + ns;
    // Size in Bytes
    map.put(stoPfx + "async_db_size", String.valueOf(si.getAsyncDBSize()));
    // Size in Bytes
    map.put(stoPfx + "sync_db_size", String.valueOf(si.getSyncDBSize()));
    // Bytes per second
    map.put(stoPfx + "read_rate", String.valueOf(si.getDiskReadRate()));
    // Bytes per second
    map.put(stoPfx + "write_rate", String.valueOf(si.getDiskWriteRate()));
    // Operations per second
    map.put(stoPfx + "read_operations_rate", String.valueOf(si.getDiskWriteOperationsRate()));
    // Operations per second
    map.put(stoPfx + "write_operations_rate", String.valueOf(si.getDiskReadOperationsRate()));
    // Performance data
    String prfPfx = "performance" + ns;
    // Inbound rate in bytes per second for the server
    map.put(prfPfx + "msg_size_in", String.valueOf(si.getInboundBytesRate()));
    // Number of inbound messages for the server
    map.put(prfPfx + "msg_count_total_in", String.valueOf(si.getInboundMessageCount()));
    // Inbound messages per second for the server as a whole
    map.put(prfPfx + "msg_count_in", String.valueOf(si.getInboundMessageRate()));
    // Outbound rate in bytes per second for the serve
    map.put(prfPfx + "msg_size_out", String.valueOf(si.getOutboundBytesRate()));
    // Number of outbound messages for the server
    map.put(prfPfx + "msg_count_total_out", String.valueOf(si.getOutboundMessageCount()));
    // Outbound messages per second for the server as a whole
    map.put(prfPfx + "msg_count_out", String.valueOf(si.getOutboundMessageRate()));
  }

  private static void mapQueues(DestinationInfo[] queues, HashMap<String, String> map) {

    for (DestinationInfo destinationInfo : queues) {
      String name = Utils.sanitize(destinationInfo.getName());
      String destPrefix = "destination" + ns + "queue" + ns + name + ns;
      mapDestination(destPrefix, map, destinationInfo);
      mapQueue(destPrefix, map, (QueueInfo) destinationInfo);
    }

  }

  private static void mapTopics(DestinationInfo[] topics, HashMap<String, String> map) {

    for (DestinationInfo destinationInfo : topics) {
      String name = Utils.sanitize(destinationInfo.getName());
      String destPrefix = "destination" + ns + "topic" + ns + name + ns;
      mapDestination(destPrefix, map, destinationInfo);
      mapTopic(destPrefix, map, (TopicInfo) destinationInfo);
    }

  }

  private static void mapDestination(String pfx, HashMap<String, String> map, DestinationInfo di) {
    // Bytes per second
    map.put(pfx + "byte_rate_in", String.valueOf(di.getInboundStatistics().getByteRate()));
    map.put(pfx + "byte_rate_out", String.valueOf(di.getOutboundStatistics().getByteRate()));
    // Total size of messages in bytes
    map.put(pfx + "byte_total_in", String.valueOf(di.getInboundStatistics().getTotalBytes()));
    map.put(pfx + "byte_total_out",
        String.valueOf(di.getOutboundStatistics().getTotalBytes()));
    // Messages per second
    map.put(pfx + "msg_rate_in", String.valueOf(di.getInboundStatistics().getMessageRate()));
    map.put(pfx + "msg_rate_out", String.valueOf(di.getOutboundStatistics().getMessageRate()));
    // Total number of messages
    map.put(pfx + "msg_total_in",
        String.valueOf(di.getInboundStatistics().getTotalMessages()));
    map.put(pfx + "msg_total_out",
        String.valueOf(di.getOutboundStatistics().getTotalMessages()));
    // Number of consumers for this destination
    map.put(pfx + "consumer_count", String.valueOf(di.getConsumerCount()));
    // Total number of pending messages for this destination
    map.put(pfx + "pending_msg_count", String.valueOf(di.getPendingMessageCount()));
    // Total size of all pending messages for this destination
    map.put(pfx + "pending_msg_size", String.valueOf(di.getPendingMessageSize()));
    // Total number of pending messagesfor this destination that were sent
    // persistently
    map.put(pfx + "pending_persistent_msg_count",
        String.valueOf(di.getPendingPersistentMessageCount()));
    // Total size of pending messagesfor this destination that were sent
    // persistently
    map.put(pfx + "pending_persistent_msg_size",
        String.valueOf(di.getPendingPersistentMessageSize()));
  }

  private static void mapQueue(String pfx, HashMap<String, String> map, QueueInfo qi) {
    // Total number of msgs that have been delivered to consumers but have not yet
    // been acked.
    map.put(pfx + "delivered_msg_count", String.valueOf(qi.getDeliveredMessageCount()));
    // Total number of msgs that have been delivered to the Q but have not yet been
    // acked.
    map.put(pfx + "transit_msg_count", String.valueOf(qi.getInTransitMessageCount()));
    // Number of active receivers on this queue.
    map.put(pfx + "receiver_count", String.valueOf(qi.getReceiverCount()));
  }

  private static void mapTopic(String pfx, HashMap<String, String> map, TopicInfo ti) {
    // Number of active durable consumers when the TopicInfo was retrieved from the
    // server
    map.put(pfx + "active_durable_count", String.valueOf(ti.getActiveDurableCount()));
    // Number of active subscribers when the TopicInfo was retrieved from the server
    map.put(pfx + "subscriber_count", String.valueOf(ti.getSubscriberCount()));
    // Number of subscriptions swhen the TopicInfo was retrieved from the server
    map.put(pfx + "subscription_count", String.valueOf(ti.getSubscriptionCount()));
  }

}