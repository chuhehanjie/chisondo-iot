package com.chisondo.iot.device.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DevTcpChannelManager {

    private static Map<String, Channel> deviceChannelMap = new ConcurrentHashMap<>();

    public static void addDeviceChannel(String deviceId, Channel channel) {
        deviceChannelMap.put(deviceId, channel);
        log.info("add tcp channel deviceId = {}, size = {}", deviceId, deviceChannelMap.size());
    }

    public static Channel getChannelByDevice(String deviceId) {
        return deviceChannelMap.get(deviceId);
    }

    public static void removeDeviceChannel(String deviceId, Channel channel) {
        Channel target = deviceChannelMap.get(deviceId);
        if (!ObjectUtils.isEmpty(target) && ObjectUtils.nullSafeEquals(channel.id(), target.id())) {
            deviceChannelMap.remove(deviceId);
            log.info("remove tcp channel deviceId = {}", deviceId);
        }
    }

    public static String removeByChannel(Channel channel) {
        Iterator<Map.Entry<String, Channel>> it = deviceChannelMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Channel> entry = it.next();
            if (ObjectUtils.nullSafeEquals(channel, entry.getValue())) {
                log.error("删除了设备[{}]", entry.getKey());
                it.remove();
                return entry.getKey();
            }
        }
        return null;
    }
}
