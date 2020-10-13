package upc.demo.util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheLoader {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<String,ChannelId> channelMapSC = new ConcurrentHashMap<>();
    public static Map<ChannelId,String> channelMapCS = new ConcurrentHashMap<>();
    //不同表就丢弃数据包 同表丢弃查询请求 currentQuery是集中器号码
//    public static String currentQuery = " ";
//    public static List<MeterReading> singleData = new ArrayList<>();
//    public static boolean singleFlag = false;

    public static void addChannel(Channel channel, String equipmentId){
        //检查是否存在
        if(CacheLoader.channelMapSC.containsKey(equipmentId)){
            ChannelId channelId =  CacheLoader.channelMapSC.get(equipmentId);
            CacheLoader.channelGroup.remove(channelId);
            CacheLoader.channelMapCS.remove(channelId);
            CacheLoader.channelMapSC.remove(equipmentId);

            CacheLoader.channelGroup.add(channel);
            CacheLoader.channelMapCS.put(channel.id(), equipmentId);
            CacheLoader.channelMapSC.put(equipmentId, channel.id());
            System.out.println("已存在设备，更新完毕");
        }
        else{
            CacheLoader.channelMapSC.put(equipmentId,channel.id());
            CacheLoader.channelMapCS.put(channel.id(), equipmentId);
            CacheLoader.channelGroup.add(channel);
            System.out.println("添加设备至登录列表");
        }
    }
    public static void removeChannel(Channel channel) throws IOException {
        if(CacheLoader.channelMapCS.get(channel.id())==null){
            System.out.println("未注册设备业已离线");
        }
        else {
            String deviceNumber =   CacheLoader.channelMapCS.get(channel.id());
            System.out.println(deviceNumber);
            String url = "http://127.0.0.1:10001/integrationTest/deleteOnlineDevice?deviceNumber="
                    + deviceNumber;
            HttpClient.sendGetRequest(url);
            System.out.println("设备已移出登录列表");
            CacheLoader.channelGroup.remove(channel.id());
            CacheLoader.channelMapSC.remove(CacheLoader.channelMapCS.get(channel.id()));
            CacheLoader.channelMapCS.remove(channel.id());


        }
    }
}