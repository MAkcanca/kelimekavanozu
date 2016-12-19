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


import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import galilei.kelimekavanozu.BuildConfig;
import galilei.kelimekavanozu.R;

/**
 * PoemPopularActivity that displays a list of tweets, showing only the tweet text.
 */
public class PoemPopularActivity extends ListActivity {

    private static final String TAG = "PoemPopularActivity";
    private static final String SEARCH_QUERY = "#kelimekavanozu";
    private MoPubAdAdapter moPubAdAdapter;
    private static final String MY_AD_UNIT_ID = BuildConfig.MOPUB_AD_UNIT_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        setUpViews();
    }

    private void setUpViews() {
        setUpPopularList();
        setUpBack();
    }

    private void setUpPopularList() {
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(SEARCH_QUERY).build();

        final TweetTimelineListAdapter timelineAdapter = new TweetTimelineListAdapter(this, searchTimeline);
        //setListAdapter(timelineAdapter);
        getListView().setEmptyView(findViewById(R.id.loading));

        final ViewBinder mopubViewBinder = new ViewBinder.Builder(R.layout.native_ad_layout)
                .mainImageId(R.id.native_ad_main_image)
                .iconImageId(R.id.native_ad_icon_image)
                .titleId(R.id.native_ad_title)
                .textId(R.id.native_ad_text)
                .build();
        MoPubNativeAdPositioning.MoPubServerPositioning adPositioning =
                MoPubNativeAdPositioning.serverPositioning();
        final MoPubNativeAdRenderer adRenderer = new MoPubNativeAdRenderer(mopubViewBinder);
        moPubAdAdapter = new MoPubAdAdapter(this, timelineAdapter, adPositioning);
        moPubAdAdapter.registerAdRenderer(adRenderer);
        setListAdapter(moPubAdAdapter);
    }

    private void setUpBack() {
        final ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.log("PopularTweets: getting back to theme chooser");
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        moPubAdAdapter.loadAds(MY_AD_UNIT_ID);
    }
}
