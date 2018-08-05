package com;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CreateQRCode {
    public static void main(String[] args) throws Exception {
        /** jar 包核心类 */
        Qrcode x = new Qrcode();
        /** 纠错等级 */
        x.setQrcodeErrorCorrect('M');
        /** N 代表数字，A 代表a-Z，B 代表其他字符 */
        x.setQrcodeEncodeMode('B');
        /** 版本号 1-40 */
        x.setQrcodeVersion(7);
        /** 二维码内容 */
        String qrData = "https://github.com/seriouszyx";
        /** 二维码尺寸 */
        int width = 67 + 12 * (7 - 1);
        int height = 67 + 12 * (7 - 1);

        /** 设置图片缓冲区 */
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        /** 依靠Java画图工具 */
        Graphics2D gs = bufferedImage.createGraphics();
        /** 设置画板相关属性 */
        gs.setBackground(Color.white);
        gs.setColor(Color.black);
        gs.clearRect(0, 0, width, height);

        /** 偏移量，不加可能导致解析出错 */
        int pixoff = 2;


        byte[] d = qrData.getBytes("gb2312");
        if (d.length > 0 && d.length < 120) {
            /** 将内容通过 QRCode 对象转化成二维数组 */
            boolean[][] s = x.calQrcode(d);

            for (int i=0; i<s.length; i++) {
                for (int j=0; j<s.length; j++) {
                    if (s[i][j]) {
                        gs.fillRect(i*3+pixoff, j*3+pixoff, 3, 3);
                    }
                }
            }
        }

        /** 绘制结束 */
        gs.dispose();
        bufferedImage.flush();

        /** 输出图片到指定路径 */
        ImageIO.write(bufferedImage, "png", new File("F:/qrcode.png"));
    }
}
