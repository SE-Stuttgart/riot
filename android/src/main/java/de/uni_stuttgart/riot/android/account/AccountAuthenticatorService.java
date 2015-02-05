/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_stuttgart.riot.android.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to authenticate FIXME complete description.
 */
public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountService";

    // Instance field that stores the authenticator object
    private AccountAuthenticator mAccountAuthenticator;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
    }

    private AccountAuthenticator getAuthenticator() {
        // Create a new authenticator object if needed
        if (mAccountAuthenticator == null) {
            mAccountAuthenticator = new AccountAuthenticator(this);
        }
        return mAccountAuthenticator;
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            return getAuthenticator().getIBinder();
        }
        return null;
    }
}
