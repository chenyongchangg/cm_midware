package upc.demo.util;

import upc.demo.Utils;


/**
 * Created by cheny on 2020/08/27
 */
public class CommandBuilder {
    public static byte[] fiveOne(String collectorNumber, String meterAddress){
        byte[] bytes = new byte[22];
        bytes[0] = (byte) 0xfe;
        bytes[1] = (byte) 0xfe;
        bytes[2] = (byte) 0x68;
        bytes[3] = (byte) 0xa0;
        System.arraycopy(Utils.bytesReverse( Utils.str2Bcd(collectorNumber)) , 0, bytes, 4,4);
        bytes[8] = (byte) 0xaa;
        bytes[9] = (byte) 0xaa;
        bytes[10] = (byte) 0xaa;
        bytes[11] = (byte) 0x51;
        bytes[12] = (byte) 0x07;

        System.arraycopy(Utils.bytesReverse( Utils.str2Bcd(meterAddress)) , 0, bytes, 13,7);
        byte[]  src =  new byte[18];
        System.arraycopy(bytes,2,src,0,18);
        bytes[20] = Utils.addCrc(src);
        bytes[21] = (byte) 0x16;
        return bytes;
    }
     public static byte[] fourOne(String collectorId){
         byte[] bytes = new byte[16];
         bytes[0] = (byte) 0xfe;
         bytes[1] = (byte) 0xfe;
         bytes[2] = (byte) 0x68;
         bytes[3] = (byte) 0xa0;
         System.arraycopy(Utils.bytesReverse( Utils.str2Bcd(collectorId)) , 0, bytes, 4,4);
         bytes[8] = (byte) 0xaa;
         bytes[9] = (byte) 0xaa;
         bytes[10] = (byte) 0xaa;
         bytes[11] = (byte) 0x41;
         bytes[12] = (byte) 0x01;
         bytes[13] = (byte) 0xaa;
         byte[]  src =  new byte[12];
         System.arraycopy(bytes,2,src,0,12);
         bytes[14] = Utils.addCrc(src);
         bytes[15] = (byte) 0x16;
         return bytes;
     }


}
