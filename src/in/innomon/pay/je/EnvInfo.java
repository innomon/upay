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

package in.innomon.pay.je;

/**
 *
 * @author ashish
 * 22-May-11: Added BDB JE Replication extensions 
 */
public class EnvInfo {
    private String envHome = "/home/ashish/abpay";
    private String balanceStore = "CashBalance";
    private String txnStore = "TxnStore";
    private boolean createAccountOnTxn = false;  // create a new UUID if not present

    // replication support
    private String groupName = "upayReplicationGroup";
    private String nodeName  = "Master";
    private String nodeHostPort = "localhost:4101";
    private String helperHosts = "localhost:4101";
    
    public String getTxnStore() {
        return txnStore;
    }

    public void setTxnStore(String txnStore) {
        this.txnStore = txnStore;
    }

    public String getBalanceStore() {
        return balanceStore;
    }

    public void setBalanceStore(String balanceStore) {
        this.balanceStore = balanceStore;
    }

    public boolean isCreateAccountOnTxn() {
        return createAccountOnTxn;
    }

    public void setCreateAccountOnTxn(boolean createAccountOnTxn) {
        this.createAccountOnTxn = createAccountOnTxn;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHelperHosts() {
        return helperHosts;
    }

    public void setHelperHosts(String helperHosts) {
        this.helperHosts = helperHosts;
    }

    public String getNodeHostPort() {
        return nodeHostPort;
    }

    public void setNodeHostPort(String nodeHostPort) {
        this.nodeHostPort = nodeHostPort;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getEnvHome() {
        return envHome;
    }

    public void setEnvHome(String envHome) {
        this.envHome = envHome;
    }

}
