package buy.fair.antidoto;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by iFrey on 15-Apr-17.
 */

public class ResultAdapter extends BaseAdapter {

    private Context context;
    private List<Result> results;

    public ResultAdapter(Context context, List<Result> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public int getCount() {
        return this.results.size();
    }

    @Override
    public Object getItem(int position) {
        return this.results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.results_list_item, parent, false);
        }

        // Set data into the view.

        TextView reasonTV = (TextView) rowView.findViewById(R.id.reasonTV);
        TextView descriptionReasonTV = (TextView) rowView.findViewById(R.id.descriptionReasonTV);
        TextView linkTV = (TextView) rowView.findViewById(R.id.linkTV);

        TextView productTV = (TextView) rowView.findViewById(R.id.productTV);
        TextView descriptionTV = (TextView) rowView.findViewById(R.id.descriptionTV);
        TextView barcodeTV = (TextView) rowView.findViewById(R.id.barcodeTV);

        TextView companyTV = (TextView) rowView.findViewById(R.id.companyTV);


        Result result = this.results.get(position);

        reasonTV.setText(result.getReasonName());
        descriptionReasonTV.setText(result.getReasonDescription());
        linkTV.setText(result.getReasonLink());
        productTV.setText(result.getElementName());
        descriptionTV.setText(result.getElementDescription());
        barcodeTV.setText(Integer.toString(result.getElementBarcode()));
        companyTV.setText(result.getCompanyName());

        linkTV.setMovementMethod(LinkMovementMethod.getInstance());

        return rowView;
    }
}
