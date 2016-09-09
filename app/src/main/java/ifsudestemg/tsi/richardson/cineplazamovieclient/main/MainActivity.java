package ifsudestemg.tsi.richardson.cineplazamovieclient.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ifsudestemg.tsi.richardson.cineplazamovieclient.R;
import ifsudestemg.tsi.richardson.cineplazamovieclient.filme.Filme;
import ifsudestemg.tsi.richardson.cineplazamovieclient.filme.FilmeAdapter;
import ifsudestemg.tsi.richardson.cineplazamovieclient.filme.InfoFilme;
import ifsudestemg.tsi.richardson.cineplazamovieclient.obtemDados.ObtemFilmesEmCartaz;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private final int REQUEST_MODULO_UM = 1;
    private final int REQUEST_MODULO_DOIS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ObtemFilmesEmCartaz.class);
        startActivityForResult(intent, REQUEST_MODULO_DOIS);

        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_MODULO_UM ){
            if(resultCode == RESULT_OK){
                List<String> filmes = (ArrayList<String>) data.getSerializableExtra("filmes");


                for (int i = 0; i < filmes.size(); i++) {
                    Log.d("filme",filmes.get(i));
                }

                // arraylist.toArray(new String[0]) //transforma um arraylist<String> em uma lista de Strings
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, filmes.toArray(new String[0]));
                listView.setAdapter(arrayAdapter);

            }else{
                Toast.makeText(this,data.getStringExtra("msgErro"),Toast.LENGTH_SHORT).show();
            }
        }else
            if(requestCode == REQUEST_MODULO_DOIS){
                if(resultCode == RESULT_OK){
                    ArrayList<Filme> filmes = data.getParcelableArrayListExtra("filmes");

                    // Utilizo uma classe personalizada que extende ArrayAdapter, para que o
                    // Arraylista possa ser utilizado na listView
                    FilmeAdapter filmeAdapter = new FilmeAdapter(this,filmes);
                    listView.setAdapter(filmeAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // Obtém o item clicado
                            Filme item = (Filme)adapterView.getAdapter().getItem(i);

                            Intent intent = new Intent(getApplicationContext(),InfoFilme.class);
                            intent.putExtra("filme",item);
                            startActivity(intent);
                        }
                    });

                }else{
                    Toast.makeText(this,data.getStringExtra("msgErro"),Toast.LENGTH_SHORT).show();
                }
            }//if
    }//onActivityResult
}//class
