package com.faith.namazvakti.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.faith.namazvakti.R;
import com.faith.namazvakti.fragments.CitySelectionFragment;
import com.faith.namazvakti.models.City;

/**
 * Created by akif on 11/05/16.
 */
public class CityViewHolder extends RecyclerView.ViewHolder {
    private View cityItemLayout;
    private TextView textViewName;

    private CitySelectionFragment.OnCitySelectedListener onCitySelectedListener;

    public CityViewHolder(View cityItemLayout, CitySelectionFragment.OnCitySelectedListener onCitySelectedListener) {
        super(cityItemLayout);

        this.cityItemLayout = cityItemLayout;
        this.onCitySelectedListener = onCitySelectedListener;

        textViewName = (TextView) cityItemLayout.findViewById(R.id.textView_item_city_name);
    }

    public void setFrom(final City city) {
        textViewName.setText(city.name);

        cityItemLayout.setOnClickListener(v -> onCitySelectedListener.onCitySelected(city));
    }
}
