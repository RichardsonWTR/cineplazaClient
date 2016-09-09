package ifsudestemg.tsi.richardson.cineplazamovieclient.filme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ifsudestemg.tsi.richardson.cineplazamovieclient.R;

/**
 * Created by richardson on 9/8/16.
 */
public class FilmeAdapter extends ArrayAdapter<Filme> {
    public FilmeAdapter(Context context, List<Filme> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Filme filme = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.filme_item, parent, false);
        }

        TextView nomeFilmeTextView = (TextView) convertView.findViewById(R.id.nomeFilme);

        nomeFilmeTextView.setText(filme.getNome());

        return convertView;
    }//getView


}//class
