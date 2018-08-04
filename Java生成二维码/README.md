#   [Java生成二维码](https://www.imooc.com/learn/531)

##  一、介绍

### 1.  理解二维码

黑点代表二进制中的1，白点代表二进制中的0，通过1和0的排列组合，在二维空间记录数据。通过图像输入设备，读取其中的内容。

### 2.  二维码分类

二维码有不同的码制，就码制的编码原理而言，通常分为三种类型：

*   线性堆叠式二维码

>   建立在一维条码的基础之上，按需要堆叠成两行或多行。

*   矩阵式二维码

>   最常用的类型。在一个矩阵空间通过黑、白像素在矩阵中的不同分布进行编码。

*   邮政码

>   通过不同长度的条进行编码，主要用于邮政编码。

##  二、    QR Code

目前流行的三大国际标准：

*   PDF417：不支持中文
*   DM：专利未公开，需支付专利费用
*   QR Code：专利公开，支持中文

JSP生成二维码的方法：

*   借助第三方 jar，如 `zxing` 和 `qrcodejar`
*   JavaScript，如 jquery.qrcode.js

##  三、    jar 包

jar 包源码链接：

*   [ZXing源码](https://github.com/zxing/zxing)
*   

jar 包下载地址：

*   [ZXing下载](https://github.com/zxing/zxing/releases)


## 四、  使用 zxing 生成解析二维码

### 1.  使用前配置

先来看一下解压后文件夹的目录：

![此处输入图片的描述][1]

core 文件夹是核心文件夹，里面包含使用二维码技术的 `.java` 文件。
javase 文件夹里面也包含了生成图片等功能的 `.java` 文件。
可是文件夹里面不含 jar 包，我们需要手动打包

### 2.  生成二维码

```
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
```

随后在F盘目录下即可看到 `img.png` 图片

![此处输入图片的描述][2]

通过微信等扫描二维码，可以看到其中的内容。

### 3.  解析二维码

下面我们自己创造解析二维码的方法

```
/** 读取二维码 */
public class ReadQRCode {
    public static void main(String[] args) {
        MultiFormatReader formatReader = new MultiFormatReader();
        File file = new File("F:/img.png");
        try {
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            HashMap hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            Result result = formatReader.decode(binaryBitmap, hints);

            System.out.println("解析结果： " + result.toString());
            System.out.println("二维码的格式类型：" + result.getBarcodeFormat());
            System.out.println("二维码文本内容： " + result.getText() );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
```

运行程序，控制台输出二维码对应的信息

![此处输入图片的描述][3]


  [1]: https://i.loli.net/2018/08/04/5b65708aeb4fc.png
  [2]: https://i.loli.net/2018/08/04/5b65a72d9c680.png
  [3]: https://i.loli.net/2018/08/04/5b65a7f8e37a9.png