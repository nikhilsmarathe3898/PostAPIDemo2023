package com.localgenie.filter;

import javax.inject.Inject;

import com.pojo.FilteredPriceResponse;

/**
 * <h>FilterPresenter</h>
 * Created by Ali on 2/2/2018.
 */

public class FilterPresenter implements FilterContract.FilterPresent
{

    @Inject
    public FilterPresenter() {
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onMinPriceSet(FilteredPriceResponse filteredPriceResponse, String min)
    {
        if(filteredPriceResponse.getMaxPrice()==0.0)
        {

            if(!min.isEmpty())
            {
                if(Double.parseDouble(min)>=filteredPriceResponse.getMinAmount() && Double.parseDouble(min)<filteredPriceResponse.getMaxAmount())
                {
                   // minPrice = Double.parseDouble(min);
                    filteredPriceResponse.setMinPrice(Double.parseDouble(min));
                    filteredPriceResponse.setMin(true);
                  //  isMin = true;
                } else {
                   // isMin = false;
                    filteredPriceResponse.setMin(false);
                   // etFilterMinPriceFrom.setText("");
                    filteredPriceResponse.setMinPrice(0.0);
                   // minPrice = 0.0;
                }
            }
        }else
        {
            if(!min.isEmpty())
            {
                if(Double.parseDouble(min)>=filteredPriceResponse.getMinAmount() && Double.parseDouble(min)<filteredPriceResponse.getMaxPrice())
                {
                  //  minPrice = Double.parseDouble(min);
                 //   isMin = true;
                    filteredPriceResponse.setMinPrice(Double.parseDouble(min));
                    filteredPriceResponse.setMin(true);
                }
                else
                {
                  /*  isMin = false;
                    etFilterMinPriceFrom.setText("");
                    minPrice = 0.0;*/
                    filteredPriceResponse.setMinPrice(0.0);
                    filteredPriceResponse.setMin(false);
                }
            }
        }
    }

    @Override
    public void onMaxPriceSet(FilteredPriceResponse filteredPriceResponse, String max)
    {
        if(filteredPriceResponse.getMaxPrice()==0.0)
        {

            if(!max.isEmpty())
            {
                if(filteredPriceResponse.isMin())
                {
                    if(Double.parseDouble(max)>filteredPriceResponse.getMinPrice() && Double.parseDouble(max)<=filteredPriceResponse.getMaxAmount())
                    {
                    /*maxPrice = Double.parseDouble(max);
                    isMax = true;*/
                        filteredPriceResponse.setMaxPrice(Double.parseDouble(max));
                        filteredPriceResponse.setMax(true);
                    } else {
                        filteredPriceResponse.setMaxPrice(0.0);
                        filteredPriceResponse.setMax(false);
                    }
                }else {
                    if(Double.parseDouble(max)>filteredPriceResponse.getMinAmount() && Double.parseDouble(max)<=filteredPriceResponse.getMaxAmount())
                    {
                    /*maxPrice = Double.parseDouble(max);
                    isMax = true;*/
                        filteredPriceResponse.setMaxPrice(Double.parseDouble(max));
                        filteredPriceResponse.setMax(true);
                    }
                    else
                    {
                   /* isMax = false;
                    etFilterMaxPriceTo.setText("");
                    maxPrice = 0.0;*/
                        filteredPriceResponse.setMaxPrice(0.0);
                        filteredPriceResponse.setMax(false);
                    }
                }

            }
        }else
        {
            if(!max.isEmpty())
            {
                if(Double.parseDouble(max)>filteredPriceResponse.getMinPrice() && Double.parseDouble(max)<=filteredPriceResponse.getMaxAmount())
                {
                   /* maxPrice = Double.parseDouble(max);
                    isMax = true;*/
                    filteredPriceResponse.setMaxPrice(Double.parseDouble(max));
                    filteredPriceResponse.setMax(true);
                }
                else
                {
                    /*isMax = false;
                    etFilterMaxPriceTo.setText("");
                    maxPrice = 0.0;*/
                    filteredPriceResponse.setMaxPrice(0.0);
                    filteredPriceResponse.setMax(false);
                }
            }
        }

    }
}
