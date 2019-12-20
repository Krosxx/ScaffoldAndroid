package cn.daqinjia.android.scaffold.compat.okhttp;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class UnsafeOkHttpClient {
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final X509TrustManager trustAllCerts = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            // Create an ssl socket factory with our all-trusting manager
            return new OkHttpClient.Builder().sslSocketFactory(new SSL(trustAllCerts), trustAllCerts).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}