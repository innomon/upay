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

import java.io.Serializable;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import javax.xml.bind.annotation.*;

import java.util.Random;

@XmlRootElement()
@Entity
public class Balance implements Serializable {
    public static final long MIN_OTP = 100001;
    public static final long MAX_OTP = 1000000;
    public static final long MILLS_IN_DAY =
        86400000; // 24*60*60*1000 = 86400000 mili sec in a Day
    public static final int MIN_MPIN = 1000;
    public static final int MAX_MPIN = 10000;
    public static final int MAX_LAST_TXNS = 3;
    public static final String CONTROL_ACCOUNT_PFIX = "_SYS_";
    public static final String CONTROL_FLOAT_ACCOUNT = "_SYS_FLOAT";
 
    @PrimaryKey
    private String accountName = null;
    private double balance = 0;
    private boolean pullTxnAllowed = true;
    private boolean isMerchant = false;
    private boolean blocked = false;
    private String[] lastTxnIDs = null;
    private long lastTxnTime = 0;
    private double totalTxnValueToday = 0;
    private double maxPerTxnValueAllowed = 5000;
    private double maxPerDayTxnValueAllowed = 5000;

    // TODO: MD5 of OTP & MPIN
    private long otp = 0;
    private long otpGenTime = 0;
    private double otpMaxAmount = 0;
    // KYC PIN for firsttime thereafter its Master PIN 
    private int mpin = 0;
    
    public Balance() {
        super();
        init();
    }

    public Balance(String accName) {
        super();
        accountName = accName;
        init();
    }

    private void init() {
        lastTxnIDs = new String[MAX_LAST_TXNS];
        for (int i = 0; i < MAX_LAST_TXNS; i++)
            lastTxnIDs[i] = "";
    }
   

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setPullTxnAllowed(boolean pullTxnAllowed) {
        this.pullTxnAllowed = pullTxnAllowed;
    }

    public boolean isPullTxnAllowed() {
        return pullTxnAllowed;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }


    public boolean isOtpValid() {
        boolean ret = false;
        long tm = System.currentTimeMillis() - otpGenTime;
        if (otp != 0 & tm < MILLS_IN_DAY) {
            ret = true;
        }
        return ret;
    }

 
    public long genOtp(double maxAmt) {
        Random rnd = new Random();
        otp = rnd.nextLong(); 
        
        if(otp < 0)
            otp *= -1;
        
        otp %= MAX_OTP;
        if (otp < MIN_OTP)
            otp += MIN_OTP;
        otpGenTime = System.currentTimeMillis();
        otpMaxAmount = maxAmt;

        return otp;
    }
    public int genMpin() {
      Random rnd = new Random();
      mpin = rnd.nextInt(MAX_MPIN);
      if(mpin < MIN_MPIN) 
          mpin += MIN_MPIN;
      
      return mpin;
      
    }
    public void setMerchantAccount(boolean isMerchant) {
        this.isMerchant = isMerchant;
    }

    public boolean isMerchantAccount() {
        return isMerchant;
    }

    public void setMpin(int mpin) {
        this.mpin = mpin;
    }

    public int getMpin() {
        return mpin;
    }

    public double getMaxPerDayTxnValueAllowed() {
        return maxPerDayTxnValueAllowed;
    }

    public void setMaxPerDayTxnValueAllowed(double maxPerDayTxnValueAllowed) {
        this.maxPerDayTxnValueAllowed = maxPerDayTxnValueAllowed;
    }

    public double getMaxPerTxnValueAllowed() {
        return maxPerTxnValueAllowed;
    }

    public void setMaxPerTxnValueAllowed(double maxPerTxnValueAllowed) {
        this.maxPerTxnValueAllowed = maxPerTxnValueAllowed;
    }

    public double getTotalTxnValueToday() {
        return totalTxnValueToday;
    }

    public void setTotalTxnValueToday(double totalTxnValueToday) {
        this.totalTxnValueToday = totalTxnValueToday;
    }

    /**
     * @param amount
     * @param otp
     * @throws TxnException
     */
    public void validateDeductAmount(double amount,
                                     long otp) throws TxnException {
       /** if(accountName.startsWith(CONTROL_ACCOUNT_PFIX))
            return;
        **/
        if(CONTROL_FLOAT_ACCOUNT.equalsIgnoreCase(accountName))
            return;
        
        if (blocked)
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_BLOCKED);

        if (!isOtpValid())
            throw new TxnException(TxnException.Error.ERR_EXPIRED_OTP);
        if (amount < otpMaxAmount)
            throw new TxnException(TxnException.Error.ERR_LOW_OTP_AMOUNT);
        if (balance < amount)
            throw new TxnException(TxnException.Error.ERR_LOW_BALANCE);
        if (this.otp != otp)
            throw new TxnException(TxnException.Error.ERR_WRONG_OTP);

    }

    /**
     * @param amount
     * Push ONUS transaction support
     * @throws TxnException
     */
    public void validateDeductAmount(double amount) throws TxnException {
                                    
        if(CONTROL_FLOAT_ACCOUNT.equalsIgnoreCase(accountName))
            return;
        
        if (blocked)
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_BLOCKED);

        if (balance < amount)
            throw new TxnException(TxnException.Error.ERR_LOW_BALANCE);

    }

    /**
     * @param amount
     * @param txnID
     * @param otp
     * @throws TxnException
     */
    public void deductAmount(double amount, String txnID,
                             long otp) throws TxnException {
        validateDeductAmount(amount, otp);
        otp = 0;
        balance -= amount;
        lastTxnTime = System.currentTimeMillis();
        appendTxnId(txnID);
    }
        /**
     * @param amount
     * @param txnID
     * Push ONUS Transaction support
     * @throws TxnException
     */
    public void deductAmount(double amount, String txnID) throws TxnException {
        validateDeductAmount(amount);
        otp = 0;
        balance -= amount;
        lastTxnTime = System.currentTimeMillis();
        appendTxnId(txnID);
    }

    public void validateAddAmount() throws TxnException {
        if (blocked)
            throw new TxnException(TxnException.Error.ERR_ACCOUNT_BLOCKED);

    }

    /**
     * @param amount
     * @param txnID
     * @throws TxnException
     */
    public void addAmount(double amount, String txnID) throws TxnException {
        validateAddAmount();
        balance += amount;
        lastTxnTime = System.currentTimeMillis();
        appendTxnId(txnID);
    }

    public String[] getLastTxnIDs() {
        return lastTxnIDs;
    }

    public long getLastTxnTime() {
        return lastTxnTime;
    }

    protected void appendTxnId(String txnID) {
        if (lastTxnIDs.length == 1) {
            lastTxnIDs[0] = txnID;
            return;
        }
        int i;
        for (i = 0; i < lastTxnIDs.length; i++) {
            if ("".equals(lastTxnIDs[i])) {
                lastTxnIDs[i] = txnID;
                return;
            }
        } // for
        // no empty space, we need to shift up
        i = 0;
        do {
            lastTxnIDs[i] = lastTxnIDs[i + 1];
        } while (++i < (lastTxnIDs.length - 1));

        lastTxnIDs[lastTxnIDs.length - 1] = txnID;
    }
}
