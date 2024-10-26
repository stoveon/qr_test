
package kr.co.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class OcrV3StartService {

    private static final Logger log = LoggerFactory.getLogger(OcrV3StartService.class);

    // aiSee ocr start api url
    private static final String url = "https://dev.comtrue.com:8089/kakao/api/v3/ocr/start";

    // aiSee ocr 라이선스 키
    private static final String license = "sample_license";

    // aiSee ocr 종류
    private static final String cardType = "general";


    private static final int timeout = 5;
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();

    // cardType: general
    public String sendStartApi() {

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        InputStream responseIs = null;

        int statusCode = 0;
        String multipartApiBody = null;


        try {

            // parameter setting
            final Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("license", license);
            paramMap.put("cardType",cardType);

            try {

                HttpClientBuilder clientBuilder = HttpClients.custom();

                // https 설정
                if (url.startsWith("https")) {
                    SSLContext sslContext = SSLContexts.custom()
                            .useProtocol("TLSv1.2")
                            .loadTrustMaterial(null, new TrustStrategy() {
                                @Override
                                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                    return true;
                                }
                            }).build();

                    clientBuilder.setSSLHostnameVerifier(
                            new NoopHostnameVerifier()).setSSLContext(sslContext);
                }

                clientBuilder.setMaxConnPerRoute(10);
                clientBuilder.setMaxConnTotal(100);

                client = clientBuilder.build();

                HttpPost post = new HttpPost(url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                // HttpMultipartMode
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                // parameters
                if (paramMap != null) {
                    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        builder.addPart(key, new StringBody(String.valueOf(value), ContentType.MULTIPART_FORM_DATA));
                    }
                }

                // requestConfig
                if (requestConfig != null) {
                    post.setConfig(requestConfig);
                }

                post.setEntity(builder.build());
                response = client.execute(post);

            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (response == null) {
                throw new ClientProtocolException("[" + url + "] response null");
            }

            statusCode = response.getStatusLine().getStatusCode();
            log.info("{} >>> statusCode :: {}", license, statusCode);

            responseIs = response.getEntity().getContent();
            multipartApiBody = IOUtils.toString(responseIs, "UTF-8");
            log.info("{} >>> result :: {}", license, multipartApiBody.toString());

        } catch (Exception e) {
            log.error("{} | {}", e, e.getMessage());
        }
        return multipartApiBody;
    }


    public String makeStringToQrImage(String startUrl) {
        String qrImageString = "";

        int qrImageHeight = 200;
        int qrImageWidth = 200;

        try {
            //UTF-8로 인코딩된 문자열을 ISO-8859-1로 생성
            startUrl = new String(startUrl.getBytes("UTF-8"),"ISO-8859-1");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // QR Code - BitMatrix: qr code 정보 생성
            BitMatrix bitMatrix = new MultiFormatWriter().encode(startUrl, BarcodeFormat.QR_CODE, qrImageWidth, qrImageHeight);

            // QR 코드 이미지의 색상 변경 가능.
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(MatrixToImageConfig.BLACK, -1);

            // QR Code - Image 생성.
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            File qrImage = new File("qr.jpg");

            ImageIO.write(bufferedImage, "jpg", qrImage);

            byte[] bytes = FileUtils.readFileToByteArray(qrImage);
            qrImageString = Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            System.out.println("e = " + e);
        }

        return qrImageString;
    }

}
