package p8.demo.p8sokoban;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Chronometer;
import android.os.SystemClock;



// declaration de notre activity heritee de Activity
public class p8_Sokoban extends Activity {

    private SokobanView mSokobanView;
    static private TextView compteur;

    private static Chronometer chronometer;

    /**
     * initialise notre activity avec le constructeur parent
     * charge le fichier main.xml comme vue de l'activite
     * recuperation de la vue une voie cree e partir de son id
     * rend visible la vue
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        mSokobanView = (SokobanView)findViewById(R.id.SokobanView);
        compteur=(TextView) findViewById(R.id.compteur);
        mSokobanView.setVisibility(View.VISIBLE);

        compteur.setText("NOMBRE DE COUPS : " + mSokobanView.compteur);
    }

    /**
     * Fonction qui lance le chronometre
     */
    static public void LanceChrono(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

    }
}