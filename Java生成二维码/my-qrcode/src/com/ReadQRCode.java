package com;

import jp.sourceforge.qrcode.QRCodeDecoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ReadQRCode {
    public static void main(String[] args) throws Exception {
        /** 将图片读取到缓冲区 */
        File file = new File("F:/qrcode.png");
        BufferedImage bufferedImage = ImageIO.read(file);

        /** 将缓冲区信息封装传递给 QRCodeDecoder */
        QRCodeDecoder qrCodeDecoder = new QRCodeDecoder();
        String result =
                new String(qrCodeDecoder.decode(new MyQRCodeImage(bufferedImage)), "gb2312");
        System.out.println(result);
    }
}
