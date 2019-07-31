package com.localgenie.youraddress;

import android.content.Context;

import com.localgenie.model.youraddress.YourAddrData;

/**
 * @author Pramod
 * @since 19-01-2018.
 */

public interface YourAddressPresenter {

    void getAddress(String auth, Context yourAddressActivity);

    void deleteAddress(String auth, String cardId, YourAddrData yourAddrDataRowItem, int adapterPosition);

    void onItemClicked(int adapterPosition);
}
