package com.localgenie.providerList;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import java.util.ArrayList;

import com.pojo.FilteredResponse;
import com.pojo.ProviderData;

/**
 * <h>ProviderListContract</h>
 * Created by Ali on 1/29/2018.
 */

public interface ProviderListContract
{
    interface providerPresenter extends BasePresenter
    {
        void onGetProviderService(String catId);
        void onGetProviderLocation(String catId);

    }

    interface providerView extends BaseView
    {
        void onSuccessData(ArrayList<ProviderData> data);
        void onFilterRemoveArray(ArrayList<FilteredResponse> filteredResponses);

        void setLatLng();

        void onNoConnectionAvailable(String message, boolean isLocation);

        void setOnItemSelected(String subCatId, String subCatName);

        void noProviderAvailable(String providerNotAvailable);

    }
}
