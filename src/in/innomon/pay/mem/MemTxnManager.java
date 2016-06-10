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

package in.innomon.pay.mem;

import in.innomon.pay.txn.AccountInfo;
import in.innomon.pay.txn.AccountInfoManager;
import in.innomon.pay.txn.DbIterator;
import in.innomon.pay.txn.Balance;
import in.innomon.pay.txn.TxnException;
import in.innomon.pay.txn.TxnFactory;
import in.innomon.pay.txn.TxnManager;
import in.innomon.pay.txn.TxnPayload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author ashish
 */
public class MemTxnManager implements TxnManager, DbIterator, AccountInfoManager, TxnFactory {
    private HashMap<String, Balance> balMap = new  HashMap<String, Balance>();
    private HashMap<String, AccountInfo> actInfoMap = new  HashMap<String, AccountInfo>();
    private HashMap<String, String> actInfoSecKeyMap = new  HashMap<String, String>();
    
    private ArrayList<TxnPayload> txnArr = new ArrayList<TxnPayload>();
    private StartupLoader starter = null;
    
    @Override
    public void beginTxn() {
        // Dummy
    }

    @Override
    public void commit() {
         // Dummy
    }

    @Override
    public void rollback() {
         // Dummy
    }

    public void setStarter(StartupLoader starter) {
        this.starter = starter;
    }

    @Override
    public Balance getBalance(String key) throws TxnException {
        return balMap.get(key);
    }

    @Override
    public Balance createBalance(String accountName) throws TxnException {
        return new Balance(accountName);
    }

    @Override
    public void updateBalance(Balance account) throws TxnException {
        balMap.put(account.getAccountName(), account);
    }

    @Override
    public void recordTxn(TxnPayload payload) throws TxnException {
        txnArr.add(payload);
    }

    @Override
    public boolean isFloaterAccount(String account) {
           return Balance.CONTROL_FLOAT_ACCOUNT.equalsIgnoreCase(account);
    }

    @Override
    public String getFloaterAccountName() {
        return Balance.CONTROL_FLOAT_ACCOUNT;
    }
    public AccountInfo getAccountInfoByAccount(String account) {
        String mail = actInfoSecKeyMap.get(account);
        
        return  (mail == null)? null: actInfoMap.get(mail);
    }
    
    @Override
    public void updateAccountInfo(AccountInfo act) {
        actInfoMap.put(act.getEmail(), act);
        actInfoSecKeyMap.put(act.getAccountName(),act.getEmail());
    }

    @Override
    public Iterator<AccountInfo> getAccountInfoIterator() {
        return actInfoMap.values().iterator();
    }

    @Override
    public Iterator<Balance> getBalanceIterator() {
        return balMap.values().iterator();
    }

    @Override
    public Iterator<TxnPayload> getTxnPayloadIterator() {
        return txnArr.iterator();
    }

    @Override
    public void start() {
        if(starter != null)
            starter.onStart(this, this);
    }

    @Override
    public void stop() {
        // dummy
    }

    @Override
    public AccountInfoManager createAccountInfoManager() {
        return this;
    }

    @Override
    public TxnManager createTxnManager() {
        return this;
    }

    @Override
    public void close() {
        //dummy
    }

    @Override
    public AccountInfo getAccountInfoForAccountName(String mobile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AccountInfo getAccountInfoForEmail(String mail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
