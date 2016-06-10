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

import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import in.innomon.pay.txn.Balance;
import in.innomon.pay.txn.TxnException;
import in.innomon.pay.txn.TxnManager;
import in.innomon.pay.txn.TxnPayload;

/**
 *
 * @author ashish
 */
public class JeTxnManager implements TxnManager {
    private Environment env;
    private String balanceStoreName;
    private String txnStoreName;

    private EntityStore balStore = null;
    private EntityStore txnStore = null;
    private com.sleepycat.je.Transaction txn = null;
    private PrimaryIndex<String, Balance> pkBal = null;
    private PrimaryIndex<String, TxnPayload> pkTxn = null;
    
    public JeTxnManager(Environment env, String balanceStoreName, String txnStoreName) {
        this.env = env;
        this.balanceStoreName = balanceStoreName;
        this.txnStoreName = txnStoreName;
    }

    @Override
    public void beginTxn() {
        if(txn == null) {
             StoreConfig entStoreConfig = new StoreConfig();
            entStoreConfig.setAllowCreate(true);
            entStoreConfig.setTransactional(true);
            balStore = new EntityStore(env, balanceStoreName, entStoreConfig);     
            txnStore = new EntityStore(env, txnStoreName, entStoreConfig); 
            txn = env.beginTransaction(null, null);
            pkBal = balStore.getPrimaryIndex(String.class, Balance.class);
            pkTxn = txnStore.getPrimaryIndex(String.class, TxnPayload.class);
        }
    }

    @Override
    public void commit() {
        txn.commit();
        close();
    }

    @Override
    public void rollback() {
        txn.abort();
        close();
    }
    @Override
    public void close() {
        if(balStore != null)
            balStore.close();
        if(txnStore != null)
            txnStore.close();
        balStore = null;
        txnStore = null;
        txn = null;
        pkTxn = null;
        pkBal = null;
            
    }
    @Override
    public Balance getBalance(String key) throws TxnException {
        return pkBal.get(txn, key, LockMode.RMW);
    }

    @Override
    public Balance createBalance(String accountName) throws TxnException {
        return new Balance(accountName);
    }

    @Override
    public void updateBalance(Balance account) throws TxnException {
        pkBal.put(txn, (Balance)account);
    }

    @Override
    public void recordTxn(TxnPayload payload) throws TxnException {
        pkTxn.put(txn, payload);
    }

    @Override
    public boolean isFloaterAccount(String accountName) {
        return Balance.CONTROL_FLOAT_ACCOUNT.equalsIgnoreCase(accountName);
    }

    @Override
    public String getFloaterAccountName() {
        return Balance.CONTROL_FLOAT_ACCOUNT;
    }

}
