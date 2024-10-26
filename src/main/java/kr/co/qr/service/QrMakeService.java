
package kr.co.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;

@Service
public class QrMakeService {

    private static final Logger log = LoggerFactory.getLogger(QrMakeService.class);

    public String makeUrlToQrCode(String startUrl) {

        String qrImageString = "";
        int qrImageHeight = 200;
        int qrImageWidth = 200;

        try {
            //UTF-8로 인코딩된 문자열을 ISO-8859-1로 생성
            startUrl = new String(startUrl.getBytes("UTF-8"), "ISO-8859-1");
            // QR Code - BitMatrix: qr code 정보 생성
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(startUrl, BarcodeFormat.QR_CODE, qrImageWidth, qrImageHeight);

            // QR 코드 이미지의 색상 변경 가능.
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(MatrixToImageConfig.BLACK, -1);

            // QR Code - Image 생성.
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);

            // QR Code - image file 생성
            File qrImage = new File("qr.jpg");
            ImageIO.write(bufferedImage, "jpg", qrImage);

            // QR Code - image to imageString
            byte[] bytes = FileUtils.readFileToByteArray(qrImage);
            qrImageString = Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            log.error("{} | {}", e, e.getMessage());
        }

        return qrImageString;
    }

}
