/**
 * Copyright (C) 2014 Twitter Inc and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package galilei.kelimekavanozu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.Twitter;

import galilei.kelimekavanozu.R;
import galilei.kelimekavanozu.SessionRecorder;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpViews();

    }

    private void setUpViews() {
        setUpSignOut();
    }

    private void setUpSignOut() {
        final TextView bt = (TextView) findViewById(R.id.deactivate_accounts);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter.getSessionManager().clearActiveSession();
                Digits.getSessionManager().clearActiveSession();
                SessionRecorder.recordSessionInactive("About: accounts deactivated");
                Answers.getInstance().logLogin(new LoginEvent().putMethod("Twitter").putSuccess(false));
                Answers.getInstance().logLogin(new LoginEvent().putMethod("Digits").putSuccess(false));

                Toast.makeText(getApplicationContext(), "All accounts are cleared",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
