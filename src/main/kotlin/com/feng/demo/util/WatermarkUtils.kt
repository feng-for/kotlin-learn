package com.feng.demo.util

import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.highgui.HighGui.imshow
import org.opencv.highgui.HighGui.waitKey
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.imwrite
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*


object WatermarkUtils {
    fun loadImage(imagePath: String?): Mat? {
        return Imgcodecs.imread(imagePath)
    }
    fun saveImage(imageMatrix: Mat?, targetPath: String?) {
        imwrite(targetPath, imageMatrix)
    }
    fun testOpenCV() {
        // 读取图像
        val image = loadImage("C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\111.jpg")
        imshow("Original Image", image)
        // 创建输出单通道图像
        val grayImage = image?.apply { Mat(image.rows(), image.cols(), CvType.CV_8SC1) }
        // 进行图像色彩空间转换
        cvtColor(image, grayImage, COLOR_RGB2GRAY);
        imshow("Processed Image", grayImage);
        imwrite("C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\222.jpg", grayImage);
        waitKey();
    }
    fun removeWatermark(){
        val src = loadImage("C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\111.jpg")
        val gray = Mat()
        cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
        val mask1 = Mat()
        val threshold = Mat()
        val edges = Mat()
        // 进行掩膜处理
        Imgproc.medianBlur(gray, mask1, 15)
        Imgproc.adaptiveThreshold(
            mask1,
            threshold,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY,
            15,
            2.0
        )
        Core.bitwise_not(threshold, threshold)
        // 进行边缘检测
        Imgproc.Canny(threshold, edges, 50.0, 200.0, 3, false)
        // 定位水印区域
        val hierarchy = Mat()
        val contours: List<MatOfPoint> = ArrayList()
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        // 标识定位到的水印区域
        for (i in contours.indices) {
            Imgproc.drawContours(src, contours, i, Scalar(0.0, 0.0, 255.0), 2)
        }
        imshow("Contours", src)
        waitKey()
        // 将水印区域覆盖为周围区域的平均像素值
        for (i in contours.indices) {
            val mask: Mat = Mat.zeros(edges.size(), CvType.CV_8UC1)
            Imgproc.drawContours(mask, contours, i, Scalar(255.0, 255.0, 255.0), -1)
            val mean = Mat()
            Core.mean(src, mask)
            if (src != null) {
                for (j in 0 until src.rows()) {
                    for (k in 0 until src.cols()) {
                        val pixel: DoubleArray = src.get(j, k)
                        if (mask[j, k][0] == 255.0) {
                            pixel[0] = mean[0, 0][0]
                            pixel[1] = mean[1, 0][0]
                            pixel[2] = mean[2, 0][0]
                            src.put(j, k, pixel[0], pixel[1], pixel[2])
                        }
                    }
                }
            }
        }
    }
    fun coverMean() {
        // 读取带有水印的图片
        val s = "C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\111.jpg"
        val image = loadImage(s)
        // 创建蒙版
        val mask = Mat()
        threshold(image, mask, 200.0, 255.0, THRESH_BINARY_INV)
        // 去除水印
        val result = Mat()

        // 保存结果
        imwrite("C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\result.jpg", result)
    }
}

fun main(){
    OpenCV.loadShared();
    val s = "C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\111.jpg"
    // WatermarkUtils.saveImage(loadImage(s), "C:\\Users\\Fengzhiwei\\Pictures\\Screenshots\\222.jpg")
    // WatermarkUtils.testOpenCV()
    WatermarkUtils.removeWatermark()
    println()
}