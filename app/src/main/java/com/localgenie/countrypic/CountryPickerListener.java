package com.localgenie.countrypic;

public interface CountryPickerListener {
  void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID, int max);
}