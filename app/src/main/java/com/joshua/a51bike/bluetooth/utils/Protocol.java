package com.joshua.a51bike.bluetooth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ============================================================
 * <p>
 * 版 权 ： 吴奇俊  (c) 2017
 * <p>
 * 作 者 : 吴奇俊
 * <p>
 * 版 本 ： 1.0
 * <p>
 * 创建日期 ： 2017/3/22 13:37
 * <p>
 * 描 述 ：
 * <p>
 * ============================================================
 **/

public class Protocol {

    private static byte[] stringTime2HexByte(String time) {
        int int_time = Integer.parseInt(time);
        String hex_time = Integer.toHexString(int_time);
        String hex_time_format = null;
        if (hex_time.length() < 2) {
            hex_time_format = "0" + hex_time;
        } else {
            hex_time_format = hex_time;
        }
        return CRC16.hexStringToBytes(hex_time_format);
    }

    /**
     * 获取协议需要的内容
     * @param deviceId 设备ID
     * @param type 开关机状态 0x01开机 0x00关机
     * @return bytes
     */
    public static byte[] getBytes(String deviceId,byte type) {
        byte[] real_byte = new byte[25];
        byte[] id_byte = CRC16.hexStringToBytes(deviceId);
        System.arraycopy(id_byte, 0, real_byte, 0, id_byte.length);
        //功能码固定0x03
        real_byte[12] = (byte) 0x03;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(date);
        String time[] = dateString.split(":");
        String hour = time[0];
        String minute = time[1];
        String second = time[2];

        real_byte[13] = stringTime2HexByte(hour)[0];
        real_byte[14] = stringTime2HexByte(minute)[0];
        real_byte[15] = stringTime2HexByte(second)[0];

        byte[] md5_time = new byte[]{real_byte[13], real_byte[14], real_byte[15], real_byte[15], real_byte[14],
                real_byte[13], real_byte[13], real_byte[14], real_byte[15], real_byte[15], real_byte[14],
                real_byte[13]};

        byte[] pre_md5_18 = new byte[18];
        for (int i = 0; i < md5_time.length; i++) {
            byte add_byte = (byte) (id_byte[i] + md5_time[i]);
            pre_md5_18[i] = add_byte;
        }
        pre_md5_18[12] = stringTime2HexByte(hour)[0];
        pre_md5_18[13] = stringTime2HexByte(minute)[0];
        pre_md5_18[14] = stringTime2HexByte(second)[0];
        pre_md5_18[15] = stringTime2HexByte(hour)[0];
        pre_md5_18[16] = stringTime2HexByte(minute)[0];
        pre_md5_18[17] = stringTime2HexByte(second)[0];

        byte[] after_md5_16 = new byte[16];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            after_md5_16 = md.digest(pre_md5_18);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        real_byte[16] = after_md5_16[10];
        real_byte[17] = after_md5_16[11];
        real_byte[18] = after_md5_16[12];
        real_byte[19] = after_md5_16[13];
        real_byte[20] = after_md5_16[14];
        real_byte[21] = after_md5_16[15];
        //0x01开机
        //0x00关机
        real_byte[22] = type;

        byte[] before_crc_byte = new byte[23];
        System.arraycopy(real_byte, 0, before_crc_byte, 0, before_crc_byte.length);

        int crc = CRC16.calcCrc16(before_crc_byte);

        String crc_str = Integer.toHexString(crc);
        String crc_str_format = null;
        if (crc_str.length() < 4) {
            crc_str_format = "0" + crc_str;
        } else {
            crc_str_format = crc_str;
        }
        String crc_23 = crc_str_format.substring(0, 2);
        String crc_23_ten = Integer.parseInt(crc_23, 16) + "";
        String crc_24 = crc_str_format.substring(2, 4);
        String crc_24_ten = Integer.parseInt(crc_24, 16) + "";

        real_byte[23] = stringTime2HexByte(crc_23_ten)[0];
        real_byte[24] = stringTime2HexByte(crc_24_ten)[0];

        return real_byte;
    }
}
