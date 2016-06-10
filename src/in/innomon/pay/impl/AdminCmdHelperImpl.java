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

import in.innomon.pay.txn.AccountInfo;
import in.innomon.pay.txn.AccountInfoManager;
import in.innomon.pay.txn.Balance;
import in.innomon.pay.txn.TxnException;
import in.innomon.pay.txn.TxnFactory;
import in.innomon.pay.txn.TxnManager;
import in.innomon.pay.txn.TxnRes;
import java.util.Vector;

/**
 *
 * @author ashish
 */
public  class AdminCmdHelperImpl implements AdminCmdHelper {

    private Vector<String> admins = new Vector<String>();
    private TxnFactory txnFact = null;
    private String defaultMMID = "7654321";  // MMID is 7 digits, 4 digits are used by NPCI for routing, 3 are in control of the bank

    public AdminCmdHelperImpl() {
    }

    public AdminCmdHelperImpl(TxnFactory txnFact) {
        this.txnFact = txnFact;
    }

    public void setTxnFactory(TxnFactory txnFact) {
        this.txnFact = txnFact;
    }

    public void setAdmin(String adminMailId) {
        if (adminMailId != null) {
            admins.add(adminMailId.trim().toUpperCase());
        }
    }

    public void setDefaultMMID(String mmid) {
        defaultMMID = mmid;
    }

    @Override
    public boolean isAdmin(String emailId) {
        boolean ret = false;
        if (emailId == null) {
            return false;
        }
        emailId = emailId.trim().toUpperCase();
        for (int i = admins.size(); i > 0; i--) {
            if ((emailId.startsWith(((String) admins.get(i - 1))))) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    @Override
    public void register(String mobile, int kycPin) throws TxnException {

        RegisterMobile regMo = new RegisterMobile(txnFact);
        regMo.register(mobile, kycPin);
    }

    @Override
    public void userRegistration(String email, String nickName, String mobile, int kycPin) throws TxnException {
        RegisterMobile regMo = new RegisterMobile(txnFact);
        regMo.userEmailRegistration(email, nickName, mobile, kycPin);
    }

    @Override
    public String getAccountName(String email) throws TxnException {
        String ret = null;
        if (email == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_DOES_NOT_EXISTS);
        }

        AccountInfoManager hlpr = txnFact.createAccountInfoManager();
        try {
            AccountInfo actInfo = hlpr.getAccountInfoForEmail(email);
            if (actInfo != null) {
                ret = actInfo.getAccountName();
            } else {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_DOES_NOT_EXISTS);
            }
        } finally {
            hlpr.close();
        }
        return ret;

    }

    @Override
    public double getAccountBalance(String accountName) throws TxnException {
        double ret = 0;
        if (accountName == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
        }

        TxnManager hlpr = txnFact.createTxnManager();
        try {
            hlpr.beginTxn();
            Balance bal = hlpr.getBalance(accountName);
            if (bal != null) {
                ret = bal.getBalance();
            } else {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
            }
            hlpr.commit();
        } finally {
            hlpr.close();
        }
        return ret;

    }

    @Override
    public String loadMoney(String accountName, double amount) throws TxnException {
        if (accountName == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
        }
        MoneyLoader loader = new MoneyLoader(txnFact);
        TxnRes res = loader.loadMoney(accountName, amount);
        String ret = res.getStatus() + ": Added to [" + res.getAddedToAccount()
                + "] Deducted from [" + res.getDeductedFromAccount()
                + "] Amount [" + res.getTxnAmount() + "] Txn ID [" + res.getTxnRefID() + "]";
        return ret;
    }

    @Override
    public long genOtp(String mobile, double maxAmount) throws TxnException {
        if (mobile == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
        }
        MoneyLoader loader = new MoneyLoader(txnFact);
        return loader.genOtp(mobile, maxAmount);
    }

    @Override
    public String unloadMoney(String accountName, double amount) throws TxnException {
        if (accountName == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
        }
        MoneyLoader loader = new MoneyLoader(txnFact);
        TxnRes res = loader.unloadMoney(accountName, amount);
        String ret = res.getStatus() + ": Added to [" + res.getAddedToAccount()
                + "] Deducted from [" + res.getDeductedFromAccount()
                + "] Amount [" + res.getTxnAmount() + "] Txn ID [" + res.getTxnRefID() + "]";
        return ret;
    }

    @Override
    public String pullMoney(String fromAccountName, String toAccountName, double amount, long otp) throws TxnException {
        if (fromAccountName == null || toAccountName == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
        }
        MoneyLoader loader = new MoneyLoader(txnFact);
        TxnRes res = loader.transferMoney(fromAccountName, toAccountName, amount, otp);
        String ret = res.getStatus() + ": Added to [" + res.getAddedToAccount()
                + "] Deducted from [" + res.getDeductedFromAccount()
                + "] Amount [" + res.getTxnAmount() + "] Txn ID [" + res.getTxnRefID() + "]";
        return ret;
    }

    @Override
    public String getPrimaryMMID(String mobile) {
        return defaultMMID;
    }
    
    @Override
    public String getAccountEmail(String accountName) throws TxnException {
        String ret = null;
        if (accountName == null) {
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_DOES_NOT_EXISTS);
        }

        AccountInfoManager hlpr = txnFact.createAccountInfoManager();
        try {
            AccountInfo actInfo = hlpr.getAccountInfoForAccountName(accountName);
            if (actInfo != null) {
                ret = actInfo.getEmail();
            } else {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_DOES_NOT_EXISTS);
            }
        } finally {
            hlpr.close();
        }
        return ret;

    }
    
}
