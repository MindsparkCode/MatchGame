package devapp.matchgame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {
    int levelPosition = 0, symbolPosition = 0, highscorePosition = 0;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Switch switchAudio = (Switch) findViewById(R.id.switchAudio);
        final Switch switchVibration = (Switch) findViewById(R.id.switchVibration);
        final Spinner spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
        final Spinner spinnerSymbol = (Spinner) findViewById(R.id.spinnerSymbol);
        final Button buttonMatch = (Button) findViewById(R.id.buttonMatch);
        final Spinner spinnerHighScores = (Spinner) findViewById(R.id.spinnerHighScores);

        preferences = getSharedPreferences("OPTIONS", MODE_PRIVATE);
        editor= preferences.edit();
        tercihleriOku(preferences, switchAudio, switchVibration);

        switchAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("AUDIO", true);
                }else {
                    editor.putBoolean("AUDIO", false);
                }
                editor.apply();
            }
        }
        );
        switchVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("VIBRATE", true);
                }else {
                    editor.putBoolean("VIBRATE", false);
                }
                editor.apply();
            }
        }
        );

        //Spinneri dolduracağım.
        //Öncelikle bir string dizisi oluşturuyor ve içini res/values/strings.xml aracılığıyla dolduruyorum.
        String[] levelDizisi = getResources().getStringArray(R.array.level);
        //Spinnerin veri alışverişi için adapter nesnesi tanımlıyorum.
        //Parametreler, conteks, listenin elemanlarının nasıl gösterileceği(kendimiz de bir xml oluşturabiliriz) ve Listeye doldurulacak veri dizisi.
        ArrayAdapter<CharSequence> adapterLevel = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_activated_1, levelDizisi);
        //adapterLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); Kullanmasan da oluyor
        spinnerLevel.setAdapter(adapterLevel);

        //Aynı işlemleri Symbolleri sergileyeceğimiz Spinner için yapıyoruz.
        final String[] symbolDizisi = getResources().getStringArray(R.array.symbol);
        ArrayAdapter<CharSequence> adapterSymbol = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_activated_1, symbolDizisi);
        spinnerSymbol.setAdapter(adapterSymbol);

        //Aynı işlemleri Skorları sergileyeceğimiz Spinner için yapıyoruz.
        final String[] highScoresDizisi = getResources().getStringArray(R.array.highScores);
        ArrayAdapter<CharSequence> adapterHighScores = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_activated_1, highScoresDizisi);
        spinnerHighScores.setAdapter(adapterHighScores);

        //Spinner elemanlarının seçiminde verilecek reaksiyonları belirliyoruz.
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                levelPosition = spinnerLevel.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSymbol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                symbolPosition = spinnerSymbol.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerHighScores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                highscorePosition=spinnerHighScores.getSelectedItemPosition();
                if (highscorePosition!=0){
                    Intent startScoreActivity = new Intent(getApplicationContext(),ScoreActivity.class);
                    startScoreActivity.putExtra("pozisyon",highscorePosition);
                    startActivity(startScoreActivity);
                    spinnerHighScores.setSelection(0);   //geri dondugumde son seçim seçili kalmasın diye
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (levelPosition == 0 && symbolPosition == 0) {
                    Toast.makeText(getBaseContext(), "Please Choose LEVEL and SYMBOL, before press START", Toast.LENGTH_SHORT).show();
                } else if (levelPosition == 0) {
                    Toast.makeText(getBaseContext(), "Please choose LEVEL", Toast.LENGTH_SHORT).show();
                } else if (symbolPosition == 0) {
                    Toast.makeText(getBaseContext(), "Please choose SYMBOL", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "LEVEL: " + spinnerLevel.getSelectedItem().toString() + "\nSYMBOL: " + spinnerSymbol.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    Intent startGameActivity = new Intent(getApplicationContext(), GameActivity.class);
                    int[] pozisyonDizisi = {levelPosition, symbolPosition};
                    startGameActivity.putExtra("pozisyonlar", pozisyonDizisi);
                    startActivity(startGameActivity);
                }
            }
        });
    }

    public void tercihleriOku(SharedPreferences preferences, CompoundButton v_1, CompoundButton v_2){
        boolean audio = preferences.getBoolean("AUDIO", true);
        boolean vibrate = preferences.getBoolean("VIBRATE", true);
        v_1.setChecked(audio);
        v_2.setChecked(vibrate);
    }
}
