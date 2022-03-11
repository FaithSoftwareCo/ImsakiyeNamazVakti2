package com.faith.namazvakti.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.faith.namazvakti.R;
import com.faith.namazvakti.fragments.CountrySelectionFragment;
import com.faith.namazvakti.models.Country;

/**
 * Created by akif on 11/05/16.
 */
public class CountryViewHolder extends RecyclerView.ViewHolder {
    private Context context;

    private View countryItemLayout;
    private TextView textViewName;
    private TextView textViewNativeName;

    private CountrySelectionFragment.OnCountrySelectedListener onCountrySelectedListener;

    public CountryViewHolder(Context context, View countryItemLayout, CountrySelectionFragment.OnCountrySelectedListener onCountrySelectedListener) {
        super(countryItemLayout);

        this.context = context;
        this.countryItemLayout = countryItemLayout;
        this.onCountrySelectedListener = onCountrySelectedListener;

        textViewName       = (TextView) countryItemLayout.findViewById(R.id.textView_item_country_name);
        textViewNativeName = (TextView) countryItemLayout.findViewById(R.id.textView_item_country_nativeName);
    }

    public void setFrom(final Country country) {
        String nameToUse = country.getLocalizedName(context);

        textViewName.setText(nameToUse);
        textViewNativeName.setText(country.nameNative);

        countryItemLayout.setOnClickListener(v -> onCountrySelectedListener.onCountrySelected(country));
    }
}
