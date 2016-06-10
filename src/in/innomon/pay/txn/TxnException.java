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

public class TxnException extends IllegalArgumentException {


    public static enum Error {
       ERR_UNDEFINED, ERR_UNSUPPORTED_TXN_TYPE, ERR_ACCOUNT_BLOCKED, ERR_EXPIRED_OTP, ERR_LOW_OTP_AMOUNT, ERR_LOW_BALANCE, ERR_WRONG_OTP, ERR_INVALID_PIN,
       ERR_ADD_TO_ACCOUNT_NOT_FOUND, ERR_DEDUCT_FROM_ACCOUNT_NOT_FOUND, ERR_ACCOUNT_EXISTS, ERR_ACCOUNT_DOES_NOT_EXISTS, ERR_ACCOUNT_INFO_DOES_NOT_EXISTS, ERR_ACCOUNT_INFO_EXIST                                                                                        
    }
    private Error error = Error.ERR_UNDEFINED;
    
    public TxnException(Throwable throwable) {
        super(throwable);
    }

    public TxnException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public TxnException(String string) {
        super(string);
    }
    public TxnException(String string, Error err) {
      super(string);
      error = err;
    }
  public TxnException(Error err) {
    super(err.toString());
    error = err;
  }

    public TxnException() {
        super();
    }
  public void setError(TxnException.Error error) {
      this.error = error;
  }

  public TxnException.Error getError() {
      return error;
  }
}
