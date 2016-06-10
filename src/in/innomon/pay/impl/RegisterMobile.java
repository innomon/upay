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

/**
 *
 * @author ashish
 */
public class RegisterMobile {

    private TxnFactory tfact = null;

    public RegisterMobile() {
    }

    public RegisterMobile(TxnFactory tfact) {
        this.tfact = tfact;
    }

    public void register(String mobile, int kycPin) throws TxnException {
// Should not be present in AccountInfo nor in Balance collections
        ensureMobileNotRegistred(mobile);
        TxnManager txnMgr = tfact.createTxnManager();
        try {
            txnMgr.beginTxn();
            Balance bal = txnMgr.getBalance(mobile);
            if (bal != null) {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_EXISTS);
            }

            Balance newAct = txnMgr.createBalance(mobile);
            newAct.setMpin(kycPin);

            txnMgr.updateBalance(newAct);
            txnMgr.commit();

        } finally {
            txnMgr.close();
        }
    }

    private void ensureMobileNotRegistred(String mobile) throws TxnException {
        AccountInfoManager actInfoMgr = tfact.createAccountInfoManager();
        try {
            AccountInfo actInfo = actInfoMgr.getAccountInfoForAccountName(mobile);
            if (actInfo != null) {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_EXIST);
            }
        } finally {
            actInfoMgr.close();
        }
    }
    // User receives KYC PIN by SMS or in person and regesters using REGISTER command from XMPP client

    public void userEmailRegistration(String email, String nickName, String mobile, int kycPin) throws TxnException {

        TxnManager txnMgr = tfact.createTxnManager();
        AccountInfoManager actInfoMgr = tfact.createAccountInfoManager();
        try {

            // Validations
            // Mobile number should not be assigned to any user
            AccountInfo actInfo = actInfoMgr.getAccountInfoForAccountName(mobile);
            if (actInfo != null) {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_INFO_EXIST);
            }

            // check KYC PIN
            txnMgr.beginTxn();
            Balance bal = txnMgr.getBalance(mobile);
            if (bal == null) {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_DOES_NOT_EXISTS);
            }
            if (bal.isBlocked()) {
                throw new TxnException(TxnException.Error.ERR_ACCOUNT_BLOCKED);
            }
            if (bal.getMpin() != kycPin) {
                throw new TxnException(TxnException.Error.ERR_INVALID_PIN);
            }

            // Create the Account
            actInfo = new AccountInfo();
            actInfo.setAccountName(mobile);
            actInfo.setEmail(email);
            actInfo.setPersonName(nickName);
            
            actInfoMgr.updateAccountInfo(actInfo);
            
            txnMgr.commit();
            
        } finally {
            txnMgr.close();
            actInfoMgr.close();
        }
    }
}
