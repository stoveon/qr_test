
package kr.co.qr.service;

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

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OcrV3ResultService {

    private static final Logger log = LoggerFactory.getLogger(OcrV3ResultService.class);
    private static final int timeout = 5;
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();


    public String sendResultApi(String token, String resultApiUrl) {

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        InputStream responseIs = null;

        int statusCode = 0;
        String multipartApiBody = null;

        try {
            // parameter setting
            final Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("token", token);

            try {
                HttpClientBuilder clientBuilder = HttpClients.custom();
                // https 설정
                if (resultApiUrl.startsWith("https")) {
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

                HttpPost post = new HttpPost(resultApiUrl);
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
                throw new ClientProtocolException("[" + resultApiUrl + "] response null");
            }

            statusCode = response.getStatusLine().getStatusCode();
            log.info("{} >>> statusCode :: {}", token, statusCode);

            responseIs = response.getEntity().getContent();
            multipartApiBody = IOUtils.toString(responseIs, "UTF-8");
            log.info("{} >>> result :: {}", token, multipartApiBody.toString());

        } catch (Exception e) {
            log.error("{} | {}", e, e.getMessage());
        }
        return multipartApiBody;
    }

//    public String sendResultApi(String token, String sessionId) {
//        Map<String, Object> result = new HashMap<>();
//
//        int statusCode = 0;
//        String multipartApiBody = null;
//        CloseableHttpResponse response = null;
//        InputStream responseIs = null;
//
//        log.info("{} | {} ___________________________________________________________________ START", token, sessionId);
//        try {
//
//            // header setting
//            final Map<String, String> headerMap = new HashMap<>();
//            headerMap.put("Set-Cookie", "COMTRUEJSESSIONID=" + sessionId);
//
//            // image setting
//            final Map<String, byte[]> binaryMap = new HashMap<>();
//
//            // parameter setting
//            final Map<String, Object> paramMap = new HashMap<>();
//
//            paramMap.put("token", token);
////            paramMap.put("Cookie", "COMTRUEJSESSIONID=" + sessionId);
//
//            response = RestApiUtil.runPost(url, headerMap, HttpMultipartMode.BROWSER_COMPATIBLE, binaryMap, paramMap, requestConfig, true);
//            if (response == null) {
//                throw new ClientProtocolException("[" + url + "] response null");
//            }
//
//            statusCode = response.getStatusLine().getStatusCode();
//
//            log.info("{} >>> statusCode :: {}", token, statusCode);
//
//            ResponseHandler<String> handler = new BasicResponseHandler();
//            StatusLine statusLine = response.getStatusLine();
//            responseIs = response.getEntity().getContent();
//            multipartApiBody = IOUtils.toString(responseIs, "UTF_8");
//
//            log.info("{} >>> result :: {}", token, multipartApiBody.toString());
//
//        } catch (Exception e) {
//            log.error("{} | {}", e, e.getMessage());
//        }
//
//        log.info("{} | {} ___________________________________________________________________ END", token, sessionId);
//
//        return multipartApiBody;
//    }
}
