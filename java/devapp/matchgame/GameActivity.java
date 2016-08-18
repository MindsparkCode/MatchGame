package devapp.matchgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class GameActivity extends Activity {

    RelativeLayout relativeLayout = null;
    GridLayout gridLayout = null;
    Chronometer chronometer = null;
    TextView textViewSkor = null;
    Button startButton = null;
    MediaPlayer ses = null;
    Vibrator titresim = null;
    Handler handler = null;
    boolean audio = true;
    boolean vibrate = true;
    int satirSayisi = 0;
    int sutunSayisi = 0;
    int bekleme = 0;
    int skorKatsayisi = 0;
    int buttonSayisi = 0;
    int cikisSayac = 0;
    String[] sembolAdlari = null;
    String sembolAdi = null;
    Drawable sembolArka = null;
    ArrayList<ImageButton> buttonArrayList = new ArrayList<ImageButton>();
    ArrayList<Drawable> sembolArrayList = new ArrayList<Drawable>();
    Runnable runnableBaslangic = null;
    boolean baslatildi = false;
    boolean oynaniyor = false;
    long duraklamaZamani = 0;
    int buttonIndex = 0;          //buttonIndex buttonId ile aynı olacak.
    int hamleSayisi = 0;
    Runnable runnableKarsilastirma = null;
    long gecenSure = 0;
    int skor = 0;
    int tmpIndex1 = 0;
    int tmpIndex2 = 0;
    String tmpSembolName1 = null;
    String tmpSembolName2 = null;
    int eslesenler = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        startButton = (Button) findViewById(R.id.startButton);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        textViewSkor = (TextView) findViewById(R.id.textViewSkor);
        ses = MediaPlayer.create(GameActivity.this, R.raw.ses);
        ses.setLooping(true);
        titresim = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();

        SharedPreferences preferences;
        preferences = getSharedPreferences("OPTIONS", Context.MODE_PRIVATE);
        audio = preferences.getBoolean("AUDIO", true);
        vibrate = preferences.getBoolean("VIBRATE", true);

        switch (getIntent().getIntArrayExtra("pozisyonlar")[0]) {
            case 1:
                satirSayisi = 4;
                sutunSayisi = 4;
                bekleme = 1500;
                skorKatsayisi = 1;
                break;
            case 2:
                satirSayisi = 6;
                sutunSayisi = 6;
                bekleme = 3250;
                skorKatsayisi = 2;
                break;
            case 3:
                satirSayisi = 8;
                sutunSayisi = 8;
                bekleme = 6000;
                skorKatsayisi = 4;
                break;
            default:
                return;
        }
        gridLayout.setRowCount(satirSayisi);
        gridLayout.setColumnCount(sutunSayisi);
        buttonSayisi = satirSayisi * sutunSayisi;

        sembolAdlari = getResources().getStringArray(R.array.drawableNames);
        switch (getIntent().getIntArrayExtra("pozisyonlar")[1]) {
            case 1:
                sembolAdi = sembolAdlari[0];
                sembolArka = ResourcesCompat.getDrawable(getResources(), R.drawable.background_country_flags, null);    //getDrawable(R.drawable.background_team_flags); api olayından ötürü bunu kullanmadım
            break;
            case 2:
                sembolAdi = sembolAdlari[1];
                sembolArka = ResourcesCompat.getDrawable(getResources(), R.drawable.background_fruit_vegetables, null);
                break;
            case 3:
                sembolAdi = sembolAdlari[2];
                sembolArka = ResourcesCompat.getDrawable(getResources(), R.drawable.background_team_flags, null);
                break;
            default:
                return;
        }

        for (buttonIndex = 0; buttonIndex < buttonSayisi; buttonIndex++) {
            imageButtonYarat(buttonArrayList, buttonIndex, gridLayout);
            gridLayout.addView(buttonArrayList.get(buttonIndex));
        }

        sembolYarat(sembolArrayList);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.getText().equals("START")) {
                    startButton.setText("STOP");
                    oynaniyor=true;
                    for (int i = 0; i < buttonArrayList.size(); i++) {
                        buttonArrayList.get(i).setClickable(true);
                    }
                    if (audio == true) {
                        ses.start();
                    }
                    chronometer.setBase(SystemClock.elapsedRealtime() + duraklamaZamani);
                    chronometer.start();
                } else {
                    startButton.setText("START");
                    oynaniyor = false;
                    for (int i = 0; i < buttonArrayList.size(); i++) {
                        buttonArrayList.get(i).setClickable(false);
                    }
                    if (ses.isPlaying()) {
                        ses.pause();
                    }
                    duraklamaZamani = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                }
            }
        });

        for (int i = 0; i < buttonSayisi; i++) {
            buttonArrayList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonIndex=v.getId();
                    buttonArrayList.get(buttonIndex).setImageDrawable(sembolArrayList.get(buttonIndex));
                    buttonArrayList.get(buttonIndex).setClickable(false);
                    hamleSayisi++;

                    if (hamleSayisi==1){
                        tmpIndex1=buttonIndex;
                        tmpSembolName1=sembolArrayList.get(buttonIndex).toString();
                    }
                    else if (hamleSayisi==2){
                        tmpIndex2=buttonIndex;
                        tmpSembolName2=sembolArrayList.get(buttonIndex).toString();
                        hamleSayisi=0;
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        runnableKarsilastirma = new Runnable() {
                            @Override
                            public void run() {
                                if (tmpSembolName1.equals(tmpSembolName2)){
                                    skor+=(50);
                                    if (vibrate){
                                        titresim.vibrate(100);
                                    }
                                    buttonArrayList.get(tmpIndex1).setVisibility(View.INVISIBLE);
                                    buttonArrayList.get(tmpIndex2).setVisibility(View.INVISIBLE);
                                    eslesenler+=2;
                                    if (eslesenler==buttonSayisi){
                                        chronometer.stop();
                                        gecenSure=SystemClock.elapsedRealtime()-chronometer.getBase();
                                        if (skor>0){
                                            skor*=skorKatsayisi;
                                            if (gecenSure<=60000){
                                                skor*=20;
                                            }
                                            else if (gecenSure>60000&&gecenSure<=120000){
                                                skor*=10;
                                            }
                                            else if (gecenSure>120000&&gecenSure<=180000){
                                                skor*=5;
                                            }
                                            else if (gecenSure>180000&&gecenSure<=240000){
                                                skor*=2;
                                            }
                                        }
                                        else {
                                            skor=0;
                                        }
                                        textViewSkor.setText(""+skor);
                                        skorKaydet(skor, chronometer.getText().toString());
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        //relativeLayout.removeAllViewsInLayout();
                                        Dialog dialog = getSkorDialog();
                                        dialog.show();
                                    }
                                }
                                else {
                                    skor-=(10);
                                    buttonArrayList.get(tmpIndex1).setImageDrawable(sembolArka);
                                    buttonArrayList.get(tmpIndex2).setImageDrawable(sembolArka);
                                    buttonArrayList.get(tmpIndex1).setClickable(true);
                                    buttonArrayList.get(tmpIndex2).setClickable(true);
                                }
                                textViewSkor.setText(""+skor);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        };
                        handler.postDelayed(runnableKarsilastirma,500);
                    }
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //RECENT BUTTON EKRAN SON GÖRÜNTÜ SORUNUNU ÇÖZMEK İÇİN BURADA YAPIYORUZ.
        if (!baslatildi) {
            Collections.shuffle(sembolArrayList);
            for (buttonIndex = 0; buttonIndex < buttonSayisi; buttonIndex++) {
                buttonArrayList.get(buttonIndex).setImageDrawable(sembolArrayList.get(buttonIndex));
            }
            runnableBaslangic = new Runnable() {
                @Override
                public void run() {
                    for (int a = 0; a < buttonArrayList.size(); a++) {
                        buttonArrayList.get(a).setImageDrawable(sembolArka);
                    }
                    if (audio == true) {
                        ses.start();
                    }
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    startButton.setText("STOP");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    baslatildi = true;
                    oynaniyor = true;
                }
            };
            handler.postDelayed(runnableBaslangic, bekleme);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if (!baslatildi){
            Toast.makeText(this, "Symbols will be shuffled again", Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(runnableBaslangic);
        }else if (oynaniyor){       //oynaniyor true ise stop işlevi üslenecek. Öbür türlü stopun yaptığı işi timer açısından iki defa yapıyordu.
            if (ses.isPlaying()) {
                ses.pause();
            }
            duraklamaZamani = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            startButton.setText("START");
            for (int i = 0; i < buttonArrayList.size(); i++) {
                buttonArrayList.get(i).setClickable(false);
            }
            oynaniyor=false;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }
    @Override
    public void onBackPressed() {
        cikisSayac++;
        if (cikisSayac==2){
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, "The current game will not be saved.\nPress a second time to exit BACK", Toast.LENGTH_SHORT).show();
            Handler handlerCikis = new Handler();
            Runnable rCikis = new Runnable() {
                @Override
                public void run() {
                    cikisSayac=0;
                }
            };
            handlerCikis.postDelayed(rCikis,3000);
        }
    }

    private void imageButtonYarat(ArrayList<ImageButton> buttonArrayList, int buttonId, GridLayout gridLayout) {
        LayoutInflater[] inflater = {(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)};
        switch (satirSayisi) {
            case 4:
                gridLayout = (GridLayout) inflater[0].inflate(R.layout.taslak_4, null);
                break;
            case 6:
                gridLayout = (GridLayout) inflater[0].inflate(R.layout.taslak_6, null);
                break;
            case 8:
                gridLayout = (GridLayout) inflater[0].inflate(R.layout.taslak_8, null);
                break;
            default:
                return;
        }
        ImageButton imageButton = (ImageButton) gridLayout.findViewById(R.id.imageButton);
        gridLayout.removeView(imageButton);
        imageButton.setId(buttonId);
        imageButton.setClickable(true);
        buttonArrayList.add(imageButton);
    }

    private void sembolYarat(ArrayList<Drawable> sembolArrayList) {
        int i = 1;
        String tmpString = sembolAdi;
        int kaynakId = 0;
        ArrayList<Integer>randomIdArrayList = new ArrayList<Integer>();
        for ( ; i<41 ;i++){
            randomIdArrayList.add(i);
        }
        i=0;
        while (i<(buttonSayisi/2)){
            Random random = new Random();
            int randomSayi = random.nextInt(40) + 1;
            if (randomIdArrayList.contains(randomSayi)){
                randomIdArrayList.remove((Integer)randomSayi);
                sembolAdi += Integer.toString(randomSayi);
                kaynakId = getResources().getIdentifier(sembolAdi,"drawable",getPackageName());
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), kaynakId, null);
                sembolArrayList.add(drawable);
                sembolArrayList.add(drawable);
                sembolAdi=tmpString;
                i++;
            }
        }
    }

    private Dialog getSkorDialog(){
        String luck=null;
        //Argüman olarak mevcut contexti alıyor. Amaç kaynak layout.xml dosyasndan gerekli nesneleri oluşturmak.
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        //Dialog penceresinin oluşturulacağı kaynak xml belirtiliyor.
        View layout = layoutInflater.inflate(R.layout.finish_activity, null);
        LinearLayout linearLayoutFinish = (LinearLayout)layout.findViewById(R.id.linearLayoutFinish);
        TextView textViewTimeSkor = (TextView)layout.findViewById(R.id.textViewTimeSkor);
        Button exitButton = (Button)layout.findViewById(R.id.buttonExit);

        //relativeLayoutFinish.removeAllViews();
        //Builder nesnesi oluşturuldu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Dialog penceresinin hangi view ile oluşturulacağı belirtildi.
        builder.setCancelable(false);
        builder.setView(linearLayoutFinish);
        switch (getIntent().getIntArrayExtra("pozisyonlar")[1]) {
            case 1:
                sembolAdi = sembolAdlari[0];
                luck=" Peace at home peace in the world... ";
                break;
            case 2:
                sembolAdi = sembolAdlari[1];
                luck=" skip the DIET just eat HEALTHY ;) ";
                break;

            case 3:
                sembolAdi = sembolAdlari[2];
                luck=" show racisim the RED CARD! ";
                break;
            default:
        }
        textViewTimeSkor.setText(luck +"\n TIME: " +chronometer.getText() +"\n SCORE WITH BONUS: " +skor  );
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    public void skorKaydet (int skor, String time){
        String klasorAdi=null;
        switch (skorKatsayisi) {
            case 1:
                klasorAdi= "SkorlarEasy";
                break;
            case 2:
                klasorAdi= "SkorlarNormal";
                break;
            case 4:
                klasorAdi= "SkorlarHard";
                break;
            default:
                return;
        }

        //Sistem belleği içerisinde uygulamanın kayıtlı olduğu yerde skorlar adında bir alt klasör oluşturdum.
        //Kontrol sağlamaya gerek yok. Klasör mevcutsa oluşturmayacak.
        //klasörün başına app_koyuyor. Yani app_SkorlarEasy gibi
        File skorlarKlasoru = getDir(klasorAdi,MODE_APPEND );


        DateFormat tarih = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat saat = new SimpleDateFormat("kk:mm");
        Date oyunZamani = new Date();
        String kayit = "SCORE: " +skor + " TIME: " +time + " DATE: " + tarih.format(oyunZamani) +" " +saat.format(oyunZamani);

        //Oluşturduğum klasör içerisinde kayıt adlı bir dosya oluşturdum.
        File skorBelgesi = new File(skorlarKlasoru.getPath(), ""+kayit);
        if (!skorBelgesi.exists()){
            try {
                skorBelgesi.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}