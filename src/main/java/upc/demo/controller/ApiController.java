package upc.demo.controller;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import upc.demo.Utils;
import upc.demo.util.*;


@RestController
@RequestMapping("/midWare")
public class ApiController {
    @GetMapping("/test")
    public Object test(){
        return CacheLoader.channelGroup.toString();
    }

    @GetMapping("/getAllMeter")
    public Object getAllMeter(
            @RequestParam String collectorNumber
    ){
        ChannelId channel = CacheLoader.channelMapSC.get(collectorNumber);
        CacheLoader.channelGroup.find(channel).writeAndFlush("41");
        return AjaxResult.success();
    }

    @Async
    @GetMapping("/singleMeterQuery")
    public Object singleMeterQuery(
            @RequestParam String concentrator,
            @RequestParam String collector,
            @RequestParam String meter

    ){
        System.out.println("收到单查请求");
        if(!CacheLoader.channelMapSC.containsKey(concentrator)){
            System.out.println("无此设备或者离线");
            return AjaxResult.error("无此设备或者离线");
        }
        if(QueryList.queryDeviceList.containsKey(concentrator)){
            System.out.println("----------------------------");
            System.out.println("设备忙");
            return AjaxResult.error("此表查询但是未返回 如果放弃查询请清空查询");
        }
        else {
            System.out.println("命令已经下发至集中器");
            QueryList.queryDeviceList.put(concentrator, meter);
            ChannelId channel = CacheLoader.channelMapSC.get(concentrator);
            ByteBuf byteBuf = Unpooled.buffer();
            Utils.printHexString(CommandBuilder.fiveOne(collector, meter));
            byteBuf.writeBytes(CommandBuilder.fiveOne(collector,meter));
            CacheLoader.channelGroup.find(channel).writeAndFlush(byteBuf);
            return AjaxResult.error("命令已经下发至集中器");
        }
    }
    //清空当前查询的标志位
    @Async
    @GetMapping("clearQuery")
    public Object clearQuery(){
        QueryList.queryDeviceList.clear();
        System.out.println("已經清空查詢");
        return AjaxResult.success("已經清空查詢");
    }

    @Async
    @PostMapping("multipleQuery")
    public Object multipleQuery(
            @RequestParam String concentrator,
            @RequestParam String collector
    ){
        System.out.println("收到多查请求");
        if(!CacheLoader.channelMapSC.containsKey(concentrator))
            return AjaxResult.error("无此设备或者离线");
        ChannelId channel = CacheLoader.channelMapSC.get(concentrator);
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(CommandBuilder.fourOne(collector));
        CacheLoader.channelGroup.find(channel).writeAndFlush(byteBuf);
        Utils.printHexString(CommandBuilder.fourOne(collector));
        return AjaxResult.success();
    }

    //backend
    @GetMapping("onlineDeviceList")
    public Object onlineDeviceList(){
        return CacheLoader.channelMapCS.values();
    }

}
