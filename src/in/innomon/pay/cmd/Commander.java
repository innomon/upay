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

package in.innomon.pay.cmd;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author ashish
 */
public class Commander implements Command  {

    private Hashtable<String, Command> cmds = new Hashtable<String, Command>();
    private Context ctx = new ContextImpl();
    
    public Commander() {
        setCommand(this);
    }

    public Context getContext() {
        return ctx;
    }
    public void setContext(Context context) {
        ctx = context;
    }
    
    public void setCommand(Command cmd) {
        cmds.put(cmd.getCmdKey().toUpperCase().trim(), cmd);
    }
    //TODO: optimize by eleminating multiple exit points

    public String executeCommand(String msg, Context helper) {
        if (msg == null || "".equals(msg)) {
            //System.out.println("Ret Help ["+msg+"]");
            return exec(msg, helper);
        }
        //System.out.println("executeCmd: ["+msg+"]");
         
        StringTokenizer tok = new StringTokenizer(msg);
        if (tok.countTokens() <= 0) {
            return exec(msg, helper);
        }
        String cmdKey = tok.nextToken().toUpperCase();
        Command cmd = cmds.get(cmdKey);
        if (cmd == null) {
            return exec(msg, helper);
        }

        return cmd.exec(msg, helper);
    }

    @Override
    public String getCmdKey() {
        return "HELP";
    }

    @Override
    public String exec(String cmdLine, Context context) {
        StringBuilder buf = new StringBuilder();
        buf.append(getCmdHelp());
        buf.append("\n");
        if (cmdLine != null && !("".equals(cmdLine))) {
            StringTokenizer tok = new StringTokenizer(cmdLine.toUpperCase());
            int cnt = tok.countTokens();
            if (tok.nextToken().equals(getCmdKey())) {
                boolean showAll = true;
                if (cnt > 1) {
                    String sub = tok.nextToken();
                    Command subCmd = cmds.get(sub);
                    buf.append(subCmd.getCmdHelp());
                    buf.append("\n");
                    showAll = false;
                }
                if (showAll) {
                    Enumeration<String> e = cmds.keys();
                    while (e.hasMoreElements()) {
                        buf.append(e.nextElement());
                        buf.append(" ");
                    }
                }
            }
        }

        return buf.toString();
    }

    @Override
    public String getCmdHelp() {
        return "Type HELP followed by command name for specific command help";
    }


}
