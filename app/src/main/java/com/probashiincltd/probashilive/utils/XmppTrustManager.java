package com.probashiincltd.probashilive.utils;

import android.util.Log;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

public class XmppTrustManager implements TrustManager {

    private final Type type;

    public enum Type {
        ACCEPT_ALL,
        ACCEPT_DEFAULT,
        ACCEPT_ONLY,
        ACCEPT_WITH,
        ACCEPT_NONE;

        Type() {

        }
    }

    public XmppTrustManager(Type type) {
        this.type = type;
    }

    public TrustManager[] getTrustManager() {
        switch (type){
            case ACCEPT_ALL: return getACCEPT_ALL();
            case ACCEPT_DEFAULT: return getACCEPT_DEFAULT();
            default: return setTrustManager();
        }
    }

    private TrustManager[] setTrustManager() {
        return new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//
//                    @Override
//                    public void checkClientTrusted(X509Certificate[]arg0, String arg1)
//                            throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(X509Certificate[]arg0, String arg1)
//                            throws CertificateException {
//                    }
//                },
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType, Socket socket)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType, Socket socket)
                            throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType, SSLEngine engine)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType, SSLEngine engine)
                            throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType)
                            throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
    }

    private TrustManager[] getACCEPT_ALL() {
        return new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType, Socket socket)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType, Socket socket)
                            throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType, SSLEngine engine)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType, SSLEngine engine)
                            throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[]
                                                           chain, String authType)
                            throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[]
                                                           chain, String authType)
                            throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
    }

    private TrustManager[] getACCEPT_DEFAULT() {
        try{
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("trustedCerts"),
                    "passphrase".toCharArray());
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(ks);
            TrustManager[] tms = tmf.getTrustManagers();
            for (TrustManager tm : tms) {
                if (tm instanceof X509ExtendedTrustManager) {
                    return new TrustManager[]{tm};
                }
            }
        } catch (Exception e) {
            Log.e("xmppTrustManager",e.toString());
        }
        return null;


    }
}
