#   [Java生成二维码](https://www.imooc.com/learn/531)



<!-- TOC -->

- [一、介绍](#一介绍)
    - [1.  理解二维码](#1--理解二维码)
    - [2.  二维码分类](#2--二维码分类)
- [二、    QR Code](#二----qr-code)
- [三、    jar 包](#三----jar-包)
- [四、  使用 zxing 生成解析二维码](#四--使用-zxing-生成解析二维码)
    - [1.  使用前配置](#1--使用前配置)
    - [2.  生成二维码](#2--生成二维码)
    - [3.  解析二维码](#3--解析二维码)
- [四、    使用 QRCode 生成解析二维码](#四----使用-qrcode-生成解析二维码)
    - [1.  生成二维码](#1--生成二维码)
    - [2.  解析二维码](#2--解析二维码)
- [五、    使用 jquery-qrcode 生成二维码](#五----使用-jquery-qrcode-生成二维码)

<!-- /TOC -->



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

jar 包下载地址：

*   [ZXing 下载](https://github.com/zxing/zxing/releases)
*   [QRCode 生成二维码下载](http://www.swetake.com/qrcode/index-e.html)
*   [QRCode 生成二维码下载](https://zh.osdn.net/projects/qrcode/)
*   [jquery-qrcode 下载](https://github.com/jeromeetienne/jquery-qrcode/releases)

在这里已经整理好了[二维码所需资源](https://github.com/seriouszyx/Mooc-and-more/tree/master/Java%E7%94%9F%E6%88%90%E4%BA%8C%E7%BB%B4%E7%A0%81/%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%89%80%E9%9C%80%E8%B5%84%E6%BA%90)。

## 四、  使用 zxing 生成解析二维码

### 1.  使用前配置

先来看一下解压后文件夹的目录：


<div align='center'>
    <img src="https://i.loli.net/2018/08/04/5b65708aeb4fc.png">
</div>


core 文件夹是核心文件夹，里面包含使用二维码技术的 `.java` 文件。

javase 文件夹里面也包含了生成图片等功能的 `.java` 文件。

可是文件夹里面不含 jar 包，可以使用 eclipse 或 Intellij idea 等 IDE 手动打包，这里整理好了[ jar 包资源](https://github.com/seriouszyx/Mooc-and-more/tree/master/Java%E7%94%9F%E6%88%90%E4%BA%8C%E7%BB%B4%E7%A0%81/%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%89%80%E9%9C%80%E8%B5%84%E6%BA%90/zxing)。

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


<div align='center'>
    <img src="https://i.loli.net/2018/08/04/5b65a72d9c680.png">
</div>


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



<div align='center'>
    <img src="https://i.loli.net/2018/08/04/5b65a7f8e37a9.png">
</div>


##  四、    使用 QRCode 生成解析二维码

这里整理了 QRCode 的[ jar 包资源](https://github.com/seriouszyx/Mooc-and-more/tree/master/Java%E7%94%9F%E6%88%90%E4%BA%8C%E7%BB%B4%E7%A0%81/%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%89%80%E9%9C%80%E8%B5%84%E6%BA%90/qrcode)，其中 **A** 为二维码生成 jar 包， **B** 为二维码解析 jar 包。

### 1.  生成二维码

这里慕课网视频中的代码有问题，二层循环中的 i 和 j 顺序相反，导致解析代码解析后会产生一连串的数字，下面是修改后的代码：

```
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
        int width = 300;
        int height = 300;

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

```


<div align='center'>
    <img src="https://i.loli.net/2018/08/05/5b6671a37c8d0.png">
</div>

这时生成的二维码似乎与我们想象中的有些不同，主要是因为宽和高的设置有问题，这里需要用公式计算尺寸

```
        int width = 67 + 12 * (7 - 1);
        int height = 67 + 12 * (7 - 1);
```

成功生成二维码！



<div align='center'>
    <img src="https://i.loli.net/2018/08/05/5b6671a37dd5d.png">
</div>


### 2.  解析二维码

```
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
```

解析结果：


<div align='center'>
    <img src="https://i.loli.net/2018/08/05/5b6672165d218.png">
</div>

##  五、    使用 jquery-qrcode 生成二维码

建立 javaweb 工程，导入 js 文件（注意引入顺序），新建 jsp 文件：

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>qrcode</title>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.qrcode.min.js"></script>
</head>
<body>
    生成的二维码如下：<br>
    <div id="qrcode"></div>

<script>
    jQuery('#qrcode').qrcode('https://github.com/seriouszyx')
</script>
</body>
</html>
```

运行服务器，打开 jsp 页面


<div align='center'>
    <img src="https://i.loli.net/2018/08/05/5b6678c63c448.png">
</div>



