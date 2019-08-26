package com.localgenie.countrypic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.localgenie.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by embed on 6/9/16.
 *
 */
public class CountryPicker extends DialogFragment implements Comparator<Country> {

    private EditText searchEditText;
    private ListView countryListView;
    private CountryListAdapter adapter;
    private List<Country> allCountriesList;
    private List<Country> selectedCountriesList;
    private CountryPickerListener listener;
    private Context context;

    public static Currency getCurrencyCode(String countryCode) {
        try {
            return Currency.getInstance(new Locale("en", countryCode));
        } catch (Exception ignored) {
        }
        return null;
    }

 /*   public EditText getSearchEditText() {
        return searchEditText;
    }

    public ListView getCountryListView() {
        return countryListView;
    }*/

    private static String readEncodedJsonString() throws java.io.IOException {
        byte[] data = Base64.decode(Constants.ENCODED_COUNTRY_CODE, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    /**
     * To support show as dialog
     */
    public static CountryPicker newInstance(String dialogTitle) {
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    private List<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<>();
                String allCountriesCode = readEncodedJsonString();
                JSONArray countryArray = new JSONArray(allCountriesCode);
                for (int i = 0; i < countryArray.length(); i++) {
                    JSONObject jsonObject = countryArray.getJSONObject(i);
                    String countryName = jsonObject.getString("name");
                    String countryDialCode = jsonObject.getString("dial_code");
                    String countryCode = jsonObject.getString("code");
                    String min_digits = jsonObject.getString("min_digits");
                    String max_digits = jsonObject.getString("max_digits");
                    Country country = new Country();
                    country.setCode(countryCode);
                    country.setName(countryName);
                    country.setDial_code(countryDialCode);
                    country.setMin_digits(min_digits);
                    country.setMax_digits(max_digits);
                    allCountriesList.add(country);
                }
                Collections.sort(allCountriesList, this);
                selectedCountriesList = new ArrayList<>();
                selectedCountriesList.addAll(allCountriesList);
                return allCountriesList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker, null);
        Bundle args = getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            getDialog().setTitle(dialogTitle);

            int width = getResources().getDimensionPixelSize(R.dimen.cp_dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.cp_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
        getAllCountries();
        searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
        countryListView = (ListView) view.findViewById(R.id.country_code_picker_listview);

        adapter = new CountryListAdapter(getActivity(), selectedCountriesList);
        countryListView.setAdapter(adapter);

        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    Country country = selectedCountriesList.get(position);
                    listener.onSelectCountry(country.getName(), country.getCode(), country.getDial_code(),
                            country.getFlag(),Integer.parseInt(country.getMax_digits()));
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    @SuppressLint("DefaultLocale") private void search(String text) {
        selectedCountriesList.clear();
        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

    public Country getUserCountryInfo(Context context) {
        this.context = context;
        getAllCountries();
        String countryIsoCode;
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!(telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT)) {
            countryIsoCode = telephonyManager.getSimCountryIso();
            for (int i = 0; i < allCountriesList.size(); i++) {
                Country country = allCountriesList.get(i);
                if (country.getCode().equalsIgnoreCase(countryIsoCode)) {
                    country.setFlag(getFlagResId(country.getCode()));
                    country.setMax_digits(country.getMax_digits());
                    return country;
                }
            }
        }
        return afghanistan();
    }

    private Country afghanistan() {
        Country country = new Country();
        country.setCode("IN");
        country.setName("India");
        country.setDial_code("+91");
        country.setMax_digits("10");
        country.setFlag(R.drawable.flag_in);
        return country;
    }

    private int getFlagResId(String drawable) {
        try {
            return context.getResources()
                    .getIdentifier("flag_" + drawable.toLowerCase(Locale.ENGLISH), "drawable",
                            context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
