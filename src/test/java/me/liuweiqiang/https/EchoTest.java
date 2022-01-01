package me.liuweiqiang.https;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

@SpringBootTest
class EchoTest {

    @Value("${server.ssl.key-store-password}")
    private String pass;
    @Value("${server.ssl.key-store-type}")
    private String type;
//    private String path = "/self.pfx";
    @Value("${server.ssl.key-store}")
    private String resource;
    @Value("${server.ssl.intermediate-cer}")
    private String intermediate;

    @Test
    public void testCreate() throws Exception {
        SSLSocketFactory.getDefault().createSocket("127.0.0.1", 8433);
    }

    @Test
    public void testHandShake() throws Exception {
        SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("127.0.0.1", 8433);
        socket.startHandshake();
    }

    @Test
    public void testEndCertHandShake() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(type);
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getFile(resource));
//        InputStream inputStream = getClass().getResourceAsStream(path);
        keyStore.load(fileInputStream, pass.toCharArray());
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(new KeyManager[]{}, trustManagerFactory.getTrustManagers(), SecureRandom.getInstanceStrong());
        SSLSocket socket = (SSLSocket) context.getSocketFactory().createSocket("127.0.0.1", 8433);
        socket.startHandshake();
    }

    @Test
    public void testSelfSignedHandShake() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getFile(intermediate));
        keyStore.load(null);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        keyStore.setCertificateEntry("intermediate", certificateFactory.generateCertificate(fileInputStream));
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(new KeyManager[]{}, trustManagerFactory.getTrustManagers(), SecureRandom.getInstanceStrong());
        SSLSocket socket = (SSLSocket) context.getSocketFactory().createSocket("127.0.0.1", 8433);
        socket.startHandshake();
    }


}