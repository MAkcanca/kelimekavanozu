package galilei.kelimekavanozu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import galilei.kelimekavanozu.R;

/**
 * Created by magpi on 12/18/16.
 */

public class GuideActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Hoşgeldin !", "Kendi özel kelime kavanozuna hoşgeldin. Kelime Kavanozu mu ? O da ne...", R.drawable.kavanoz, Color.parseColor("#FE3EEE")));

        addSlide(AppIntroFragment.newInstance("Bir gün,\n Bir kelime", "Her gün yatmadan önce, o gün yaptıklarınızı düşünüp, sizi en çok mutlu eden, en çok heyecanlandıran, düne göre bugün sizi en çok değiştiren şey her ne ise onu en iyi ifade edecek kelimeyi bulup, kelime kavanozunuza atın.", R.drawable.world, Color.parseColor("#172e49")));
        addSlide(AppIntroFragment.newInstance("Kelimeler Dünyayı Değiştirebilir", "Kelime Kavanozu 'nun bütün gelirleri, ihtiyaç sahiplerine bağışlanmaktadır.", R.drawable.world, Color.parseColor("#172e49")));


    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(GuideActivity.this, InitialActivity.class);
        startActivity(i);
    }

}
