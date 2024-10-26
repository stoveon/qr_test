/*
package kr.co.qr.util;

import lombok.extern.slf4j.Slf4j;
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

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Slf4j
public class HttpUtil {

    private static CloseableHttpClient getClient(String url, boolean useConnectionManager) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        log.info("URL ===== {}", url);

        HttpClientBuilder clientBuilder = HttpClients.custom();
        CloseableHttpClient client;
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
            clientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier()).setSSLContext(sslContext);
        }
        if (useConnectionManager) {
            clientBuilder.setMaxConnPerRoute(10);
            clientBuilder.setMaxConnTotal(100);
        }
        client = clientBuilder.build();
        return client;
    }

    public static CloseableHttpResponse runPost(
            String url,
            Map<String, String> headerMap,
            HttpMultipartMode multipartMode,
            Map<String, byte[]> binaryMap,
            Map<String, Object> paramMap,
            RequestConfig requestConfig,
            boolean useConnectionManager
    ) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getClient(url, useConnectionManager);
            HttpPost post = new HttpPost(url);

            // header
            if (headerMap != null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    post.setHeader(key, value);
//                    post.setHeader(new BasicHeader(key, value));
                }
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // HttpMultipartMode
            if (multipartMode != null) {
                builder.setMode(multipartMode);
            }

            // binaryBody
            if (binaryMap != null) {
                for (Map.Entry<String, byte[]> entry : binaryMap.entrySet()) {
                    builder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.DEFAULT_BINARY, entry.getKey() + ".jpg");
                }
            }

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
            log.info("{} ## HttpStatus {}", url, response.getStatusLine().getStatusCode());
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
        return response;
    }

}
*/
