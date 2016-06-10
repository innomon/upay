/*
* Copyright (c) 2016, BON BIZ IT Services Pvt LTD.
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* Author: Ashish Banerjee, tech@innomon.in
*/

package in.innomon.pay.xmpp;

import in.innomon.util.LifeCycle;
import in.innomon.pay.cmd.Commander;
import in.innomon.pay.cmd.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 *
 * @author ashish
 */
public class XmppBot implements XmppBotHelper, Runnable {

    private String xmppServer = "gmail.com";
    private String clientId = "your.name@gmail.com";
    private String clientPass = "";
    private Commander cmdr = new Commander();
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean terminate = false;
    private int workerThreads = 2;
    private XMPPConnection con;
    private long snooze = 1000; // millisecs
    private boolean debug = false;
    private LifeCycle lifeCycle = null;
    
     private  ExecutorService execServ = null;
    
    public XmppBot() {
    }
    public void setDBLifeCycle(LifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public void setExecServ(ExecutorService execServ) {
        this.execServ = execServ;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public void setSnooze(long snooze) {
        this.snooze = snooze;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }
    
    @Override
    public void run() {
                    if(execServ == null) {
                synchronized(this) {
                execServ = Executors.newFixedThreadPool(workerThreads);
                }
            }  
        if (debug) {
            XMPPConnection.DEBUG_ENABLED = true;
        }
        try {
            

            con = getConnection();
            con.connect();
            con.login(clientId, clientPass);
            if(lifeCycle != null)
                lifeCycle.start();
           
            PacketFilter filter = new PacketFilter() {

                @Override
                public boolean accept(Packet packet) {
                    if (!(packet instanceof Message)) {
                        return false;
                    }
                    Message.Type messageType = ((Message) packet).getType();
                    return messageType != Message.Type.groupchat
                            && messageType != Message.Type.headline;
                }
            };

            PacketCollector collector = con.createPacketCollector(filter);
            while (!terminate) {
                Message msg = (Message) collector.nextResult(snooze);
                if (msg != null) {
                     execServ.submit(new XmppMsgProcessor(this, msg));
                }
            }
        } catch (Exception e) {
            log.severe(e.toString());
            if (debug) {
                e.printStackTrace();
            }
        } finally {
            terminate = true;
            if (con != null) {
                con.disconnect();
            }
            con = null;
            if(lifeCycle != null)
                lifeCycle.stop();
        }
    }

    protected XMPPConnection getConnection() throws Exception {
   /*             //SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            //SASLAuthentication.supportSASLMechanism("DIGEST-MD5", 0);
            ConnectionConfiguration config = new ConnectionConfiguration(xmppServer, 5222,xmppServer);
            
            con = new XMPPConnection(config);
            config.setSASLAuthenticationEnabled(true);
             
            //con = new XMPPConnection(xmppServer);
*/
        
        return new XMPPConnection(xmppServer);
    }
    @Override
    public void requestTermination() {
        terminate = true;
    }

    @Override
    public boolean isTerminationRequested() {
        return terminate;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        if (workerThreads >= 1) {
            this.workerThreads = workerThreads;
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientPass() {
        return clientPass;
    }

    public void setClientPass(String clientPass) {
        this.clientPass = clientPass;
    }

    @Override
    public Commander getCommander() {
        return cmdr;
    }

    public void setCommander(Commander cmdr) {
        this.cmdr = cmdr;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    public void setLogger(Logger log) {
        this.log = log;
    }

    public String getXmppServer() {
        return xmppServer;
    }

    public void setXmppServer(String xmppServer) {
        this.xmppServer = xmppServer;
    }

    @Override
    public Context getContext() {
        return cmdr.getContext();
    }


    @Override
    public void sendMessage(Message msg) {
        msg.setFrom(con.getUser());
        // this gets queued in PacketWriter queue
        con.sendPacket(msg);
    }

    @Override
    public long getSnoozeTimeMillis() {
        return snooze;
    }

    public void setSnoozeTimeMillis(long tmMillis) {
        this.snooze = tmMillis;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean dbg) {
        debug = dbg;
    }
}
