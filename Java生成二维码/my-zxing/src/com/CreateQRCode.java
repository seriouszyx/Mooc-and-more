package com;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/** 生成二维码 */
public class CreateQRCode {
    public static void main(String[] args) {
        /** 二维码大小 */
        int width = 300;
        int height = 300;
        /** 二维码格式 */
        String format = "png";
        /** 二维码内容 */
        String content = "https://github.com/seriouszyx";

        /**
         *  定义二维码参数
         *
         *  CHARACTER_SET           编码类型
         *  ERROR_CORRECTION        纠错等级
         *      L < M < Q < H  纠错能力越高，可存储的越少
         *  MARGIN                  边距
         **/
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);

        /** 生成二维码 */
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path file = new File("F:/img.png").toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
