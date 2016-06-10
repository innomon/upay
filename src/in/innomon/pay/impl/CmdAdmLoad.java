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
public class CmdAdmLoad extends CmdAdminBase {
    private Pattern patNums = Pattern.compile("[0-9]*");
    private Pattern patMail = Pattern.compile("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
    
    public CmdAdmLoad() {
        cmdName = "LOAD";
        help = "ADMIN Load <email or mobile> <amount>";        
    }
    @Override
    public String exec(String cmdLine, Context cmdCtx) {
         AdminCmdHelper helper = getAdminHelper();
        String msgSender =  getSenderId(cmdCtx);
        if(msgSender == null)
            return "ERROR: Internal Error Message Sender Not set in context XmppConstants.CTX_XMPP_MSG_SENDER";
        if(helper == null)
            return "ERROR: Internal Error, missing AdminCmdHelper implemenation"; 
        if(!helper.isAdmin(msgSender))
            return "ERROR: Sorry You ["+msgSender+"] Do not have Admin Rights.";
        
        int ndx = cmdLine.toUpperCase().indexOf(cmdName);
        if(ndx < 0) {
            return "INVALID Command ["+cmdLine+"]"+ getCmdHelp();
        }
        String sub = cmdLine.substring(ndx);
        StringTokenizer tok = new StringTokenizer(sub);
        ndx = tok.countTokens();
        if(ndx != 3) {
             return "INVALID Arguments\n"+ getCmdHelp();
        }
        tok.nextToken(); // skip the command
        String param = tok.nextToken();
        String amtVal = tok.nextToken();
        
        String mobile = null, mail = null;
        if(param.length() == 10 && patNums.matcher(param).matches() )
            mobile = param;
        
        if(patMail.matcher(param).matches())
           mail = param;
        String ret = "OK";
        
        if(mail == null && mobile == null)
            return "ERROR format of ["+param+"] should either be a 10 digit mobile or an email id";
        if(!patNums.matcher(amtVal).matches())
            return "ERROR second param ["+amtVal+"] must be a number";
        try {
            if(mobile == null)
                mobile = helper.getAccountName(mail);
            ret = helper.loadMoney(mobile, Double.parseDouble(amtVal));
        }catch (TxnException ex) {
            cmdCtx.getLogger().severe(ex.toString());
            ret = ex.getError().name(); 
        }
        return ret;

    }

}
