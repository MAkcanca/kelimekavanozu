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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import galilei.kelimekavanozu.R;
import galilei.kelimekavanozu.ShakeDetector;
import galilei.kelimekavanozu.kelimeler.AddNoteActivity;
import galilei.kelimekavanozu.kelimeler.KelimelerAdapter;
import galilei.kelimekavanozu.kelimeler.Note;

public class ThemeChooserActivity extends AppCompatActivity {
    public static final String IS_NEW_POEM = "ThemeChooser.IS_NEW_POEM";
    KelimelerAdapter adapter;
    List<Note> notes = new ArrayList<>();
    RecyclerView recyclerView;
    long initialCount;
    int modifyPos = -1;
    FloatingActionButton fab;
    String inputLine;
    ImageView arkaplan500,kavanoz;
    ImageButton Share;
    String output, output2;
    boolean hasAccelerometer = false;
    boolean shakemode = false;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    boolean notclick = true;

    private static final String SEARCH_QUERY = "#kelimekavanozu";


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Accelerometer check
        PackageManager manager = getPackageManager();
        hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        setContentView(R.layout.activity_theme_chooser);
        SugarContext.init(this);
        arkaplan500 = (ImageView) findViewById(R.id.arkaplan500);
        kavanoz = (ImageView) findViewById(R.id.cannonball_logo);
        recyclerView = (RecyclerView) findViewById(R.id.main_list);

        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);


        recyclerView.setLayoutManager(gridLayoutManager);
        initialCount = Note.count(Note.class);
        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");

        if (initialCount >= 0) {

            notes = Note.listAll(Note.class);

            adapter = new KelimelerAdapter(ThemeChooserActivity.this, notes);
            recyclerView.setAdapter(adapter);

        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_add_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

            fab.setImageDrawable(drawable);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Crashlytics.log("Yeni Kelime: butona basıldı");
//                Answers.getInstance().logCustom(new CustomEvent("Ekle butonuna basıldı"));
                Intent i = new Intent(ThemeChooserActivity.this, AddNoteActivity.class);
                startActivity(i);

            }
        });
        if (isNetworkConnected()) {
            new arkaplan().execute();
        }
        // Handling swipe to delete



        setUpViews();
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove swiped item from list and notify the RecyclerView

                final int position = viewHolder.getAdapterPosition();
                final Note note = notes.get(viewHolder.getAdapterPosition());
                notes.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(position);

                note.delete();
                initialCount -= 1;

                Snackbar.make(fab, "Kelime silindi", Snackbar.LENGTH_SHORT)
                        .setAction("GERİ AL", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                note.save();
                                notes.add(position, note);
                                adapter.notifyItemInserted(position);
                                initialCount += 1;

                            }
                        })
                        .show();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.SetOnItemClickListener(new KelimelerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("Main", "click");

                Intent i = new Intent(ThemeChooserActivity.this, AddNoteActivity.class);
                i.putExtra("isEditing", true);
                i.putExtra("note_title", notes.get(position).title);
                i.putExtra("note", notes.get(position).note);
                i.putExtra("note_time", notes.get(position).time);

                modifyPos = position;

                startActivity(i);
            }
        });
        if (hasAccelerometer) {
            kavanoz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // View element to be shaken
                    // Perform animation
                    if (notclick) {
                        kavanoz.startAnimation(shake);
                        shakemode = true;
                        notclick = false;
                        Snackbar.make(fab, "Rastgele kelimelerden birini görmek için telefonunuzu sallayın.", Snackbar.LENGTH_LONG).show();
                        kavanoz.setColorFilter(Color.argb(100, 255, 140, 0));
                    } else {
                        shakemode = false;
                        notclick = true;
                        kavanoz.setColorFilter(getResources().getColor(R.color.green));

                    }
                }
            });
            // ShakeDetector initialization
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();

            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int count) {
                    if (shakemode) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                        // View element to be shaken
                        // Perform animation
                        Crashlytics.log("Shake event : triggered");
                        Answers.getInstance().logCustom(new CustomEvent("Shake event : tetiklendi"));
                        kavanoz.startAnimation(shake);
                        new rastgeletweet().execute();
                    }
                }
            });
        }
    }

    private void setUpViews() {
        setUpHistory();
        setUpPopular();
        setUpIcon();
    }

    private void setUpIcon() {
        final ImageView icon = (ImageView) findViewById(R.id.cannonball_logo);
//        icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Crashlytics.log("ThemeChooser: clicked About button");
//                Answers.getInstance().logCustom(new CustomEvent("clicked about"));
//                final Intent intent = new Intent(ThemeChooserActivity.this, AboutActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void setUpPopular() {
        final ImageView popular = (ImageView) findViewById(R.id.popular);
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.log("ThemeChooser: clicked Popular button");
                Answers.getInstance().logCustom(new CustomEvent("clicked popular"));
                Intent intent = new Intent(ThemeChooserActivity.this, PoemPopularActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpHistory() {
        final ImageView history = (ImageView) findViewById(R.id.history);
//        history.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Crashlytics.log("ThemeChooser: clicked History button");
//                Answers.getInstance().logCustom(new CustomEvent("clicked history"));
//                final Intent intent = new Intent(ThemeChooserActivity.this,
//                        PoemHistoryActivity.class);
//                intent.putExtra(IS_NEW_POEM, false);
//                startActivity(intent);
//            }
//        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ThemeChooserActivity.this, Settings.class);
                startActivity(intent);
            }
        });
    }

//    private void setUpThemes() {
//        final ListView view = (ListView) findViewById(R.id.theme_list);
//        view.setAdapter(new ThemeAdapter(this, Theme.values()));
//        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final Theme theme = Theme.values()[position];
//                Crashlytics.log("ThemeChooser: clicked on Theme: " + theme.getDisplayName());
//                Crashlytics.setString(App.CRASHLYTICS_KEY_THEME, theme.getDisplayName());
//                Answers.getInstance().logCustom(new CustomEvent("clicked build poem").putCustomAttribute("theme", theme.getDisplayName()));
//                final Intent intent = new Intent(getBaseContext(), PoemBuilderActivity.class);
//                intent.putExtra(PoemBuilderActivity.KEY_THEME, theme);
//                startActivity(intent);
//            }
//        });
//    }
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt("modify", modifyPos);
}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        modifyPos = savedInstanceState.getInt("modify");
    }

    @Override
    protected void onResume() {
        super.onResume();

        final long newCount = Note.count(Note.class);

        if (newCount > initialCount) {
            // A note is added
            Log.d("Main", "Adding new note");

            // Just load the last added note (new)
            Note note = Note.last(Note.class);

            notes.add(note);
            adapter.notifyItemInserted((int) newCount);

            initialCount = newCount;
        }

        if (modifyPos != -1) {
            notes.set(modifyPos, Note.listAll(Note.class).get(modifyPos));
            adapter.notifyItemChanged(modifyPos);
        }
        if (hasAccelerometer) {
            // Register the Session Manager Listener onResume
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onPause() {
        // Unregister the Sensor Manager onPause
        if (hasAccelerometer) {
            mSensorManager.unregisterListener(mShakeDetector);
        }
        super.onPause();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFormat(long date) {
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(date));
    }


    private class arkaplan extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL yahoo = null;
            try {
                yahoo = new URL("http://demirreklamist.com/nano/index.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                yahoo.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                while ((inputLine = in.readLine()) != null){
                    output = inputLine;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("") && !result.trim().equals("")) {
                Picasso
                        .with(ThemeChooserActivity.this)
                        .load(result)
                        .into(arkaplan500);
            }
            else {
                Picasso
                        .with(ThemeChooserActivity.this)
                        .load(R.drawable.th_mystery_01)
                        .into(arkaplan500);
            }
        }

    }

    private class rastgeletweet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL yahoo = null;
            try {
                yahoo = new URL("http://demirreklamist.com/nano/tweet.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                yahoo.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                while ((inputLine = in.readLine()) != null) {
                    output2 = inputLine;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output2;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("") && !result.trim().equals("")) {
                long newTime = System.currentTimeMillis();
                Note note2 = new Note("#KelimeKavanozu", result, newTime);
                note2.save();

                final long newCount2 = Note.count(Note.class);


                // A note is added
                Log.d("Main", "Adding new note");

                // Just load the last added note (new)
                Note note = Note.last(Note.class);

                notes.add(note);
                adapter.notifyItemInserted((int) newCount2);

                initialCount = newCount2;
            } else {
// Tweet gelmedi
            }
        }

    }

}
