package com.localgenie.promocode;

import com.localgenie.home.BaseView;
import com.pojo.PromoCodeResponse;

/**
 * Created by Ali on 6/6/2018.
 */
public interface PromoCodeContract
{

    interface PromoPresent extends BaseView
    {
        void onViewMoreClicked(PromoCodeResponse.PromoCodeData promoCodeData);
        void onPromoCodeSelected(String promoCode);
    }
}
