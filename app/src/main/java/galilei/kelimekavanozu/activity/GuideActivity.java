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

        addSlide(AppIntroFragment.newInstance("Hoşgeldin !", "Kendi özel kelime kavanozuna hoşgeldin. Kelime Kavanozu mu ? O da ne...", R.drawable.kavanoz, Color.parseColor("#FC9D9A")));
        addSlide(AppIntroFragment.newInstance("Bir gün,\n Bir kelime", "Her gün yatmadan önce, o gün yaptıklarınızı düşünüp, sizi en çok mutlu eden, en çok heyecanlandıran, düne göre bugün sizi en çok değiştiren şey her ne ise onu en iyi ifade edecek kelimeyi bulup, kelime kavanozunuza atın.", R.drawable.night, Color.parseColor("#172e49")));
        addSlide(AppIntroFragment.newInstance("Neden ?", "Her gün öyle bir kelime seçmelisiniz ki, o gün yaşadığınız deneyimleri, mutlulukları, hüzünleri içinde toplasın. Bu sayede kelimelerin gerçek anlamlarını daha iyi anlayacaksınız.", R.drawable.question, Color.parseColor("#B3D416")));
        addSlide(AppIntroFragment.newInstance("Her Gece", "Eğer isterseniz, yatmadan önce kavanozunuz size yazmayı hatırlatabilir.", R.drawable.clock, Color.parseColor("#95dfbc")));
        addSlide(AppIntroFragment.newInstance("Kelimeler Dünyayı Değiştirebilir", "Kelime Kavanozu 'nun bütün gelirleri, ihtiyaç sahiplerine bağışlanmaktadır.", R.drawable.world, Color.parseColor("#172e49")));
        addSlide(AppIntroFragment.newInstance("Hadi Başlayalım !", "Kelime Kavanozu kullanmak sizde ve etrafınızdaki insanlarda iyi sonuçlara yol açacaktır. Bize katıldığınız için teşekkür ederiz :) !", R.drawable.duck, Color.parseColor("#25c6aa")));



    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startLoginActivity();
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
