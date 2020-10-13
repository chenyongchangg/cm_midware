package upc.demo.util;

import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cheny on 2020/09/06
 */
public class QueryList {
    public static Map<String, String> queryDeviceList = new ConcurrentHashMap<>();
}
