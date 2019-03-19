package com.chisondo.iot.device.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

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

    public static void remoteDeviceChannel(String deviceId, Channel channel) {
        Channel target = deviceChannelMap.get(deviceId);
        if (!ObjectUtils.isEmpty(target) && ObjectUtils.nullSafeEquals(channel.id(), target.id())) {
            deviceChannelMap.remove(deviceId);
            log.info("remove tcp channel deviceId = {}", deviceId);
        }
    }
}
