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

package in.innomon.pay.impl;

import in.innomon.pay.txn.Transactor;
import in.innomon.pay.txn.Balance;
import in.innomon.pay.txn.TxnException;
import in.innomon.pay.txn.TxnFactory;
import in.innomon.pay.txn.TxnManager;
import in.innomon.pay.txn.TxnPayload;
import in.innomon.pay.txn.TxnReq;
import in.innomon.pay.txn.TxnRes;

/**
 * 
 * @author ashish
 */
public class MoneyLoader {
    private TxnFactory tfact;
    
    public MoneyLoader(TxnFactory fact) {
        this.tfact = fact;
    }
    
    public TxnRes loadMoney(String accountName, double amount) throws TxnException {
        TxnPayload txpl = new TxnPayload();
        TxnManager txnMgr = tfact.createTxnManager();
        try {
            String floater = txnMgr.getFloaterAccountName();
            TxnReq req = txpl.getReq();
            req.setTxnAmount(amount);
            req.setDeductFromAccount(floater);
            req.setAddToAccount(accountName);
            req.setTxnRefID(java.util.UUID.randomUUID().toString());
            Transactor txor = new Transactor();
            txor.transact(txnMgr, txpl);
        } finally {
            txnMgr.close();
        }
        return txpl.getRes();
    }
      public TxnRes unloadMoney(String accountName, double amount) throws TxnException {
        TxnPayload txpl = new TxnPayload();
        TxnManager txnMgr = tfact.createTxnManager();
        try {
            String floater = txnMgr.getFloaterAccountName();
            TxnReq req = txpl.getReq();
            req.setTxnAmount(amount);
            req.setDeductFromAccount(accountName);
            req.setAddToAccount(floater);
            req.setTxnRefID(java.util.UUID.randomUUID().toString());
            Transactor txor = new Transactor();
            txor.transact(txnMgr, txpl);
            
        } finally {
            txnMgr.close();
        }
        return txpl.getRes();
    }
      public TxnRes transferMoney(String fromAccountName, String toAccountName, double amount, long otp) throws TxnException {
        TxnPayload txpl = new TxnPayload();
        TxnManager txnMgr = tfact.createTxnManager();
        try {
            TxnReq req = txpl.getReq();
            req.setTxnAmount(amount);
            req.setDeductFromAccount(fromAccountName);
            req.setAddToAccount(toAccountName);
            req.setOtp(otp);
            req.setTxnRefID(java.util.UUID.randomUUID().toString());
            Transactor txor = new Transactor();
            txor.transact(txnMgr, txpl);
            
        } finally {
            txnMgr.close();
        }
        return txpl.getRes();
    }
    public long genOtp(String mobile, double amount) throws TxnException {
        long otp = 0;
        TxnManager txnMgr = tfact.createTxnManager();
        try {
            txnMgr.beginTxn();
            Balance bal = txnMgr.getBalance(mobile);
            if(bal == null) {
                txnMgr.rollback();
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
            }
            otp = bal.genOtp(amount);
            txnMgr.updateBalance(bal);
            txnMgr.commit();
        } finally {
            txnMgr.close();
        }
        return otp;
    }  
}
