package ifsudestemg.tsi.richardson.cineplazamovieclient.filme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.InputStream;

import ifsudestemg.tsi.richardson.cineplazamovieclient.R;

public class InfoFilme extends YouTubeBaseActivity {
    private ImageView imageView;
    private TextView nomeFilmeTextView;
    private TextView urlVideoTextView;
    private TextView generoFilmeTextView;
    private TextView duracaoFilmeTextView;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_filme);

        imageView = (ImageView) findViewById(R.id.imageView_capaFilme);
        nomeFilmeTextView = (TextView) findViewById(R.id.textView_nomeFilme);
        //urlVideoTextView = (TextView) findViewById(R.id.textView_urlVideo);
        generoFilmeTextView = (TextView) findViewById(R.id.textView_generoFilme);
        duracaoFilmeTextView = (TextView) findViewById(R.id.textView_duracaoFilme);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.customView_youtubeVideo);

        Intent intent = getIntent();
        final Filme filme = intent.getParcelableExtra("filme");
        exibirDados(filme);
        //Toast.makeText(this,filme.toString(),Toast.LENGTH_SHORT).show();

        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                String nomeFilme = filme.getUrlVideo();
                Log.d("link",nomeFilme);

                String videoId = nomeFilme.split("/v/")[1].split("\\?")[0];

                youTubePlayer.loadVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(),"Erro ao carregar o vídeo",Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void iniciarVideo(View view){
        youTubePlayerView.initialize(API_KEY,onInitializedListener);

    }
    private void exibirDados(Filme filme) {
        //Configura a imagem
        new DownloadImageFromInternet(imageView).execute(filme.getUrlCartaz());
        nomeFilmeTextView.setText(filme.getNome());
       // urlVideoTextView.setText(filme.getUrlVideo());
        generoFilmeTextView.setText(filme.getInfoExtra().get(0));
        duracaoFilmeTextView.setText(filme.getInfoExtra().get(1));
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Carregando dados...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}//class