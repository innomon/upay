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

package in.innomon.pay.je;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import in.innomon.pay.txn.AccountInfo;
import in.innomon.pay.txn.AccountInfoManager;

/**
 *
 * @author ashish
 */
public class JeAccountInfoManager implements AccountInfoManager {

    private EntityStore balStore = null;
    private StoreConfig entStoreConfig = null;
    private PrimaryIndex<String, AccountInfo> pkInfo = null;
    private SecondaryIndex<String, String, AccountInfo> skInfo = null;

    public JeAccountInfoManager(Environment env, String balanceStoreName) {
        entStoreConfig = new StoreConfig();
        entStoreConfig.setAllowCreate(true);
        entStoreConfig.setTransactional(true);
        balStore = new EntityStore(env, balanceStoreName, entStoreConfig);
        pkInfo = balStore.getPrimaryIndex(String.class, AccountInfo.class);
        skInfo = balStore.getSecondaryIndex(pkInfo, String.class, "accountName");
    }

    @Override
    public void updateAccountInfo(AccountInfo act) {
        pkInfo.put(act);
    }

    @Override
    public void close() {
        if (balStore != null) {
            balStore.close();
        }
        balStore = null;
    }

    @Override
    public AccountInfo getAccountInfoForAccountName(String mobile) {
        return skInfo.get(mobile);
    }

    @Override
    public AccountInfo getAccountInfoForEmail(String mail) {
        return pkInfo.get(mail);
    }
}
