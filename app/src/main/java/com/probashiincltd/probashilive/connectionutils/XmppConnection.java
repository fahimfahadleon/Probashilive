package com.probashiincltd.probashilive.connectionutils;

import android.content.Context;
import android.util.Log;

import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.utils.XmppTrustManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.X509TrustManager;

public class XmppConnection {
    Context context;

    String userid;
    String pass;

    protected static ConnectionListener connectionListener;
    protected static AbstractXMPPConnection mConnection;

    public XmppConnection(Context context, String userid, String pass){
        this.context = context;
        this.userid = userid;
        this.pass = pass;
        execute(userid,pass);

    }



    void execute(String user,String password){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            XMPPTCPConnectionConfiguration config = null;
            XMPPTCPConnectionConfiguration.Builder builder;
            XmppTrustManager manager = new XmppTrustManager(XmppTrustManager.Type.ACCEPT_ALL);
            try {
                builder = XMPPTCPConnectionConfiguration.builder()
//                        .setServiceName(JidCreate.domainBareFrom(Configurations.getHostName()))
                        .setXmppDomain(Configurations.getHostName())
                        .setHost(Configurations.getHostName())
                        .enableDefaultDebugger()
                        .setPort(Configurations.getPort())
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.required)

                        .setCustomX509TrustManager((X509TrustManager) manager.getTrustManager()[0])

                        .setConnectTimeout(10000)
                        .setCompressionEnabled(true);

                config = builder.build();

            } catch (IOException e) {
                Log.e("XmppOonnectionEx",e.toString());
            }
            XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
            XMPPTCPConnection.setUseStreamManagementDefault(true);
            mConnection = new XMPPTCPConnection(config);
            try {
                mConnection.addConnectionListener(connectionListener);
                mConnection.connect();
            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
//                Functions.showSweetAlertError(context,"Error","Failed To Connect!");
                Log.e("xmppConnection",e.toString());
            }
        });
    }


}
