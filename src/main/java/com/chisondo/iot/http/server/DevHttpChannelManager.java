package com.chisondo.iot.http.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DevHttpChannelManager {

    private static Map<String, Channel> httpChannelMap = new ConcurrentHashMap<>();

    public static void addHttpChannel(String deviceId, Channel channel) {
        httpChannelMap.put(deviceId, channel);
        log.info("add http channel deviceId = {}, size = {}", deviceId, httpChannelMap.size());
    }

    public static Channel getChannelByDeviceId(String deviceId) {
        return httpChannelMap.get(deviceId);
    }

    public static void removeByDeviceId(String deviceId) {
        if (httpChannelMap.containsKey(deviceId)) {
            httpChannelMap.remove(deviceId);
        }
    }
    public static void removeHttpChannel(String deviceId, Channel channel) {
        Channel target = httpChannelMap.get(deviceId);
        if (!ObjectUtils.isEmpty(target) && ObjectUtils.nullSafeEquals(channel.id(), target.id())) {
            httpChannelMap.remove(deviceId);
            log.info("remove http channel deviceId = {}", deviceId);
        }
    }
}
