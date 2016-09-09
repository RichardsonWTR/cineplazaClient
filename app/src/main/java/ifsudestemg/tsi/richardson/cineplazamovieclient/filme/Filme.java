package ifsudestemg.tsi.richardson.cineplazamovieclient.filme;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richardson on 9/8/16.
 */
public class Filme implements Parcelable{
    private String nome;
    private String urlVideo;
    private String urlCartaz;
    private List<String> infoExtra;

    public Filme(){
        infoExtra = new ArrayList<>();
    }

    public Filme(String nome, String urlVideo, String urlCartaz, List<String> infoExtra) {
        this.nome = nome;
        this.urlVideo = urlVideo;
        this.urlCartaz = urlCartaz;
        this.infoExtra = infoExtra;
    }

    protected Filme(Parcel in) {
        nome = in.readString();
        urlVideo = in.readString();
        urlCartaz = in.readString();
        infoExtra = in.createStringArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getUrlCartaz() {
        return urlCartaz;
    }

    public void setUrlCartaz(String urlCartaz) {
        this.urlCartaz = urlCartaz;
    }

    public List<String> getInfoExtra() {
        return infoExtra;
    }

    public void setInfoExtra(List<String> infoExtra) {
        this.infoExtra = infoExtra;
    }

    @Override
    public String toString() {
        return "Filme{" +
                "nome='" + nome + '\'' +
                ", urlVideo='" + urlVideo + '\'' +
                ", urlCartaz='" + urlCartaz + '\'' +
                ", infoExtra=" + infoExtra +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(nome);
        parcel.writeString(urlVideo);
        parcel.writeString(urlCartaz);
        parcel.writeStringList(infoExtra);
    }

    public static final Creator<Filme> CREATOR = new Creator<Filme>() {
        @Override
        public Filme createFromParcel(Parcel in) {
            return new Filme(in);
        }

        @Override
        public Filme[] newArray(int size) {
            return new Filme[size];
        }
    };
}//filme
