package devapp.matchgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.Arrays;

public class ScoreActivity extends Activity {
    TextView textView = null;
    ListView listView =null;
    String dizin;
    File klasor = null;
    String[] dosyaYollari=null;
    String [] dosyaYollariReverse=null;
    ArrayAdapter<String> skorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        textView = (TextView)findViewById(R.id.textViewBaslik);
        listView=(ListView)findViewById(R.id.listViewSkorlar);

        //Uygulamanın sistem belleği içerisindeki dizinini string olarak aldım.  /data/data/devapp.matchgame
        dizin=getApplicationInfo().dataDir;

        switch (getIntent().getIntExtra("pozisyon",0)){
            case 1:
                textView.setText(R.string.HGE);
                dizin+="/app_SkorlarEasy";
                break;
            case 2:
                textView.setText(R.string.HGN);
                dizin+="/app_SkorlarNormal";
                break;
            case 3:
                textView.setText(R.string.HGH);
                dizin+="/app_SkorlarHard";
                break;
            default:
                return;
        }

        klasor = new File(dizin);
        if (!klasor.exists()){
            Toast.makeText(getBaseContext(), "There is no completed game for this level", Toast.LENGTH_SHORT).show();
        }
        else {
            //Dosya adları string dizisine kaydedildi ve sıralandı(küçükten büyüğe)
            dosyaYollari = klasor.list();
            Arrays.sort(dosyaYollari);

            dosyaYollariReverse=new String[dosyaYollari.length];
            for (int a=0, b=dosyaYollari.length; a<dosyaYollari.length; a++,b--){
                dosyaYollariReverse[a]=dosyaYollari[b-1];
            }
            skorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dosyaYollariReverse);
            listView.setAdapter(skorAdapter);
        }
    }
}



