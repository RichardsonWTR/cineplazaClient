/*
* Baixa o conteúdo do site http://cineplaza.com.br utilizando HttpURLConnection em uma tarefa
* assíncrona, processa o conteúdo com Jsoup, monta uma lista com alguns dados dos filmes em cartaz
* e a retorna por Intent. (O objeto guardado na lista implementa Parcelable).
*/
package ifsudestemg.tsi.richardson.cineplazamovieclient.obtemDados;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ifsudestemg.tsi.richardson.cineplazamovieclient.R;
import ifsudestemg.tsi.richardson.cineplazamovieclient.filme.Filme;

public class ObtemFilmesEmCartaz extends AppCompatActivity {
    private final String SITE =  "http://cineplaza.com.br/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //moduloUm();
        moduloDois();
    }//onCreate

    public void moduloUm(){
        Intent intent = new Intent();

        String resultado;
        try {
            resultado = downloadContent("http://cineplaza.com.br");

            if(resultado != null) {
                Document doc = Jsoup.parse(resultado);
                Elements spans = doc.getElementsByTag("span");
                List<String> filmes = new ArrayList<>();
                for (int i = 0; i < spans.size(); i++) {
                    filmes.add(spans.get(i).html());
                }

                intent.putExtra("filmes", (Serializable) filmes);
                setResult(RESULT_OK, intent);
            }else{
                throw new ObtemFilmesException("Não foi possível obter o conteúdo");
            }

        } catch (ObtemFilmesException e) {
            intent.putExtra("msgErro",e.getMessage());
            setResult(RESULT_CANCELED,intent);
        }finally {
            finish();
        }

    }

    public void moduloDois(){
        Intent intent = new Intent();
        List<Filme> filmes = new ArrayList<>();
        String resultado;
        try {
            resultado = downloadContent(SITE);

            if(resultado != null) {
                Document doc = Jsoup.parse(resultado);

                /*
                * Graças a bela estrura do site http://cineplaza.com.br, faço este processamento
                * manual do conteúdo, que é totalmente (des)ordenado com tabelas.
                * getElementsByTag("table").get(1) retorna a tabela com os filmes
                * getElementsByTag("tr") retorna as colunas
                * */
                Elements trs = doc.getElementsByTag("table").get(1).getElementsByTag("tr");
                int tamanho = trs.size();
                //Log.d("Tamanho ",String.valueOf(tamanho));
                // Ignorando a primeira e a última referência, pois não contêm filmes.
                for (int i = 1; i < tamanho-1; i++) {
                    Filme filme = new Filme();
                    //Log.d("filme",trs.get(i).html());
                    // Obtém todas as TDs, contendo:
                    // posicao 0: O poster do filmenome
                    // pos 1: O vídeo do filme
                    // pos 2: Informações extras
                    Elements td = trs.get(i).getElementsByTag("td");

                    //Obtendo o link da imagem
                    Element imgFilmeTd = td.get(0).getElementsByTag("img").get(0);
                    //Log.d("ImgFilme",SITE + imgFilmeTd.attributes().get("src"));
                    filme.setUrlCartaz(SITE + imgFilmeTd.attributes().get("src"));

                    //Obtendo o link do video
                    Element videoFilmeTd = td.get(1).getElementsByTag("embed").get(0);
                    //Log.d("Video",videoFilmeTd.attributes().get("src"));
                    filme.setUrlVideo(videoFilmeTd.attributes().get("src"));

                    Element extraInfoTd = td.get(2);
                    Elements paragrafos = extraInfoTd.getElementsByTag("p");

                    //Obtendo o nome do filme
                    //Log.d("Nome",paragrafos.get(0).getElementsByTag("span").get(0).html());
                    filme.setNome(paragrafos.get(0).getElementsByTag("span").get(0).html());

                    List<String> infoExtra = new ArrayList<>();

                    //Informação do Genero do filme
                    //Log.d("Genero",paragrafos.get(1).getElementsByTag("font").get(0).html());
                    infoExtra.add(paragrafos.get(1).getElementsByTag("font").get(0).html());

                    //Informação da duração do filme
                    //Log.d("Duração",paragrafos.get(2).getElementsByTag("font").get(0).html());
                    //Log.d("Extrainfo",extraInfoTd.toString());

                    infoExtra.add(paragrafos.get(2).getElementsByTag("font").get(0).html());

                    filme.setInfoExtra(infoExtra);


                    filmes.add(filme);

                    //Log.d("Filme",filme.toString());
                    //Log.d("Filme Construido",filme.getNome());
                }//for

                intent.putParcelableArrayListExtra("filmes", (ArrayList<? extends Parcelable>) filmes);
                setResult(RESULT_OK, intent);
            }else{
                throw new ObtemFilmesException("Não foi possível obter o conteúdo");
            }

        } catch (ObtemFilmesException e) {
            intent.putExtra("msgErro",e.getMessage());
            setResult(RESULT_CANCELED,intent);
        }finally {
            finish();
        }
    }//moduloDois

    public String downloadContent(String url) throws ObtemFilmesException {
        if(!isNetworkAvailable()) {
            throw new ObtemFilmesException("Sem conexão à Internet");
        }

        try {
            return new DownloadPageTask().execute(url).get();

        } catch (InterruptedException e) {

           throw new ObtemFilmesException("Inesperado: Interrupted Exception");

        } catch (ExecutionException e) {

            throw new ObtemFilmesException("Inesperado: Execution Exception");

        }
    }//downloadContent

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }//isNetworkAvailable

    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try{
                return downloadUrl(urls[0]);
            }catch(IOException e){
                return null;
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            InputStream inputStream = null;
            // Apenas mostra os primeiros 500 caracteres da pagina
            //int len = 500;

            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(1000); // milliseconds
                conn.setConnectTimeout(15000); //milliseconds
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                //Inicia a query
                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d("ResponseCode", String.valueOf(responseCode));
                inputStream = conn.getInputStream();

                //Converte o inputStream em string
                return readIt(inputStream);
            }finally {
                if(inputStream != null)
                    inputStream.close();
            }
        }// downloadUrl

        //Le um número limitado de caracteres do input stream  e retorna como string
        public String readIt(InputStream inputStream, int length) throws IOException{
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return new String(buffer);
        }//readIt

        // Lê um input stream por completo e o retorna como string
        public String readIt(InputStream inputStream) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String line = bufferedReader.readLine();
            while(line != null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        }//readIt
    }//innerclass

    public class ObtemFilmesException extends Exception {
        public ObtemFilmesException(String message) {
            super(message);
        }
    }
}//class
