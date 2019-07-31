package com.localgenie.filter;

import com.localgenie.home.BasePresenter;

import com.pojo.FilteredPriceResponse;

/**
 * <h>FilterContract</h>
 * Created by Ali on 1/31/2018.
 */

public interface FilterContract
{
    interface FilterPresent extends BasePresenter
    {

        void onMinPriceSet(FilteredPriceResponse filteredPriceResponse, String min);

        void onMaxPriceSet(FilteredPriceResponse filteredPriceResponse, String max);
    }

}
