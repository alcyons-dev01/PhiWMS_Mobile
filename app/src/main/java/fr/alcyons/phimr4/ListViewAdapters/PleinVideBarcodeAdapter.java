package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import fr.alcyons.phimr4.R;

public class PleinVideBarcodeAdapter  extends ArrayAdapter {
    public List<String> referenceDesignation;
    Context context;
    public PleinVideBarcodeAdapter(Context context, List<String> referenceDesignation) {
        super(context, 0, referenceDesignation);
        this.referenceDesignation = referenceDesignation;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_demande_pleinvide_barcode, parent, false);
        }
        PleinVideBarcodeAdapterViewHolder viewHolder = (PleinVideBarcodeAdapterViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new PleinVideBarcodeAdapterViewHolder();
            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            convertView.setTag(viewHolder);
        }

        String designationCourante = (String) getItem(position);

        viewHolder.designationProduit.setText(designationCourante);

        return convertView;
    }

    public class PleinVideBarcodeAdapterViewHolder{
        TextView designationProduit;
    }
}