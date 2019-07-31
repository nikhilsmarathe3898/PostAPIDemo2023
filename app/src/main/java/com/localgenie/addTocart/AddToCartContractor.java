package com.localgenie.addTocart;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import java.util.ArrayList;

import com.pojo.CartModifiedData;
import com.pojo.ServiceResponse;

/**
 * <h>AddToCartContractor</h>
 * Created by Ali on 2/7/2018.
 */

public interface AddToCartContractor
{
    interface presenter extends BasePresenter
    {

        void onSubServiceApiCalled(String catId, String proId);

        void addSubCartData(String catId, String serviceId, int action, int serviceType);

        void getCartServiceCall(String catId);
    }
    interface ContractView extends BaseView
    {
        void onViewMore(double unit, String name, String desc);
        void onCartModified(String id, int count, int serviceType);
        void onSuccessSubService(ArrayList<ServiceResponse.ServiceDataResponse> response);

        void onCartModifiedSuccess(CartModifiedData.DataSelected data);

        void onAlreadyAddedCart(CartModifiedData.DataSelected data, ArrayList<ServiceResponse.ServiceDataResponse> serviceResponse, boolean b);

        void removeHourly();

        void addHourly(CartModifiedData.DataSelected data);

        void removeFixed();
        
        void onAlreadyCartPresent(String message, boolean isCartPresnet);

        void onGuestToLogin();

        void showAlert(int position, int quantity);
    }
}
