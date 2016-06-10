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

package in.innomon.pay.txn;

import com.sleepycat.persist.model.Persistent;
import java.io.Serializable;

@Persistent
public class TxnHdr implements Serializable {
    private String initiaorIP;
    private String initiatorDeviceType;

    public static enum WireSecurity {
        NONE,
        SSL,
        TLS;
    }

    public static enum MsgSecurity {
        NONE,
        PKI;
    }
    
    public static enum TxnType {
        PULL_ONUS,
        PULL_OFFUS,
        PUSH_ONUS,
        PUSH_OFFUS,
        CASH,
        CHARGES,
        INTEREST,
        REVERSAL,
        OTHER;
    }
    public static enum TxnChannel {
        SMS,
        ATM,
        MICROATM,
        POS,
        XMPP,
        HTTP,
        ADMIN;
    }
    private WireSecurity wireSecuirty = WireSecurity.NONE;
    private MsgSecurity msgSecurity = MsgSecurity.NONE;
    private TxnType txnType = TxnType.PULL_ONUS;
    private TxnChannel txnChannel = TxnChannel.ADMIN;
    
    public void setInitiaorIP(String initiaorIP) {
        this.initiaorIP = initiaorIP;
    }

    public String getInitiaorIP() {
        return initiaorIP;
    }

    public void setInitiatorDeviceType(String initiatorDeviceType) {
        this.initiatorDeviceType = initiatorDeviceType;
    }

    public String getInitiatorDeviceType() {
        return initiatorDeviceType;
    }

    public MsgSecurity getMsgSecurity() {
        return msgSecurity;
    }

    public void setMsgSecurity(MsgSecurity msgSecurity) {
        this.msgSecurity = msgSecurity;
    }

    public TxnChannel getTxnChannel() {
        return txnChannel;
    }

    public void setTxnChannel(TxnChannel txnChannel) {
        this.txnChannel = txnChannel;
    }

    public TxnType getTxnType() {
        return txnType;
    }

    public void setTxnType(TxnType txnType) {
        this.txnType = txnType;
    }

    public WireSecurity getWireSecuirty() {
        return wireSecuirty;
    }

    public void setWireSecuirty(WireSecurity wireSecuirty) {
        this.wireSecuirty = wireSecuirty;
    }

    public TxnHdr() {
        super();
    }

}
