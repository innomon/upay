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

import in.innomon.event.EventCentral;
import in.innomon.pay.event.TxnOccuredEvent;

public class Transactor {

    private long delta = 0;

    public Transactor() {
        super();
    }

    public void transact(TxnManager txnMgr, TxnPayload txnPayload) {

        switch (txnPayload.getHdr().getTxnType()) {
            case PULL_ONUS:
            case PUSH_ONUS:
            case CASH:
            case CHARGES:
            case REVERSAL:
                transactOnUs(txnMgr, txnPayload);
                break;
            default:
                txnPayload.getRes().setStatus(TxnException.Error.ERR_UNSUPPORTED_TXN_TYPE.toString());
        }
    }

    /**
     * @param txnPayload
     */
    private void transactOnUs(TxnManager txnMgr, TxnPayload txnPayload) {
        long start = System.nanoTime();

        if (validatePayload(txnPayload)) {
            String deductFrmAcct = txnPayload.getReq().getDeductFromAccount();
            String addToAcct = txnPayload.getReq().getAddToAccount();
            double amt = txnPayload.getReq().getTxnAmount();
            long otp = txnPayload.getReq().getOtp();
            String txnRef = txnPayload.getReq().getTxnRefID();
            txnMgr.beginTxn();
            Balance deductBal = txnMgr.getBalance(deductFrmAcct);
            Balance addBal = txnMgr.getBalance(addToAcct);

            TxnRes res = txnPayload.getRes();
            res.setAddedToAccount(addToAcct);
            res.setDeductedFromAccount(deductFrmAcct);
            res.setTxnAmount(amt);
            res.setTxnRefID(txnRef);
            res.setTxnTime(System.currentTimeMillis());

            if (addBal == null) {
                if (txnMgr.isFloaterAccount(addToAcct)) {
                    addBal = txnMgr.createBalance(addToAcct);
                } else {
                    txnMgr.rollback();
                    res.setStatus(TxnException.Error.ERR_ADD_TO_ACCOUNT_NOT_FOUND.toString());
                    delta = System.nanoTime() - start;
                    return;
                }
            }

            if (deductBal == null) {
                if (txnMgr.isFloaterAccount(deductFrmAcct)) {
                    deductBal = txnMgr.createBalance(deductFrmAcct);
                } else {
                    txnMgr.rollback();
                    res.setStatus(TxnException.Error.ERR_DEDUCT_FROM_ACCOUNT_NOT_FOUND.toString());
                    delta = System.nanoTime() - start;
                    return;
                }
            }

            try {
                addBal.validateAddAmount();
                TxnHdr.TxnType typ = txnPayload.getHdr().getTxnType();
                if (typ == TxnHdr.TxnType.PULL_ONUS) {
                    deductBal.validateDeductAmount(amt, otp);
                    deductBal.deductAmount(amt, txnRef, otp);
                } else {
                    deductBal.validateDeductAmount(amt);
                    deductBal.deductAmount(amt, txnRef);
                }
                addBal.addAmount(amt, txnRef);
                txnMgr.updateBalance(deductBal);
                txnMgr.updateBalance(addBal);
                res.setStatus(TxnRes.OK);
                txnMgr.recordTxn(txnPayload);
                txnMgr.commit();
                EventCentral.publish(new TxnOccuredEvent(txnPayload));

            } catch (TxnException ex) {
                txnMgr.rollback();
                res.setStatus(ex.getError().toString());
                return;
            }
        } else {
            txnPayload.getRes().setStatus("ERROR: Faild validation");
        }
        delta = System.nanoTime() - start;
    }

    public long getTransactionNanoTime() {
        return delta;
    }

    private boolean validatePayload(TxnPayload txnPayload) {
        if (isNullorEmpty(txnPayload.getReq().getAddToAccount())) {
            return false;
        }
        if (isNullorEmpty(txnPayload.getReq().getDeductFromAccount())) {
            return false;
        }
        if (isNullorEmpty(txnPayload.getReq().getTxnRefID())) {
            return false;
        }
        return true;
    }

    private boolean isNullorEmpty(String str) {
        return (str == null || "".equals(str));
    }
}
