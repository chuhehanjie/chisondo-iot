package com.chisondo.iot.device.server;

import com.chisondo.iot.common.Device;
import io.netty.channel.Channel;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceChannelManager {

    private static Map<Device, Channel> deviceChannelMap = new ConcurrentHashMap<>();

    public static void addDeviceChannel(Device device, Channel channel) {
        deviceChannelMap.put(device, channel);
        System.out.println("deviceChannelMap size = " + deviceChannelMap.size());
    }

    public static Channel getChannelByDevice(Device device) {
        return deviceChannelMap.get(device);
    }

    public static Device getDeviceById(Integer id) {
        for (Device device : deviceChannelMap.keySet()) {
            if (ObjectUtils.nullSafeEquals(id, device.getId())) {
                return device;
            }
        }
        return null;
    }

    public static Device getDeviceById(String id) {
        return getDeviceById(Integer.valueOf(id));
    }

    public static void remoteDeviceChannel(Device device, Channel channel) {
        Channel target = deviceChannelMap.get(device);
        if (!ObjectUtils.isEmpty(target) && ObjectUtils.nullSafeEquals(channel.id(), target.id())) {
            deviceChannelMap.remove(device);
        }
    }
}
