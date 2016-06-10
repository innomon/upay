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

import in.innomon.pay.cmd.Context;
import in.innomon.pay.txn.TxnException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 *
 * @author ashish
 */
public class CmdRegister extends CmdAdminBase {
    private Pattern patNums = Pattern.compile("[0-9]*");
    private Pattern patMail = Pattern.compile("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
    
    public CmdRegister() {
        cmdName = "REGISTER";
        help = "REGISTER <mobile> <KYC PIN received> <Your Nick Name>";
    }
    
    @Override
    public String exec(String cmdLine, Context cmdCtx) {
    AdminCmdHelper helper = getAdminHelper();
        String msgSender =  getSenderId(cmdCtx);
        if(msgSender == null)
            return "ERROR: Internal Error Message Sender Not set in context XmppConstants.CTX_XMPP_MSG_SENDER";
        if(!patMail.matcher(msgSender).matches())
            return "INVALID email id ["+msgSender+"]";
        
        if(helper == null)
            return "ERROR: Internal Error, missing AdminCmdHelper implemenation"; 
      
        int ndx = cmdLine.indexOf(cmdName);
        if(ndx < 0) {
            return "INVALID Command ["+cmdLine+"]"+ getCmdHelp();
        }
        String sub = cmdLine.substring(ndx);
        StringTokenizer tok = new StringTokenizer(sub);
        ndx = tok.countTokens();
        if(ndx != 4) {
             return "INVALID Arguments\n"+ getCmdHelp();
        }
        tok.nextToken(); // skip the command
        String mobile = tok.nextToken();
        String kycPin = tok.nextToken();
        String nickName = tok.nextToken();
        
        if(mobile.length() != 10)
            return "INVALID Mobile number ["+mobile+"] please provide 10 digit number";
        if(!patNums.matcher(mobile).matches())
            return "INVALID Mobile number ["+mobile+"]";
        if(!patNums.matcher(kycPin).matches())
            return "INVALID KYC Pin, must be int ["+kycPin+"]";
        String ret = "OK";
        try {
            helper.userRegistration(msgSender, nickName, mobile,Integer.parseInt(kycPin));
            ret = "SUCCESS: Registered email["+msgSender +" As ["+nickName+"] mobile["+mobile+"]"; 
        }catch (TxnException ex) {
            cmdCtx.getLogger().severe(ex.toString());
            ret = ex.getError().name(); 
        }
        return ret;
    }
 


}
