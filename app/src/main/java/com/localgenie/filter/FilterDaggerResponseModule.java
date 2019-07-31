package com.localgenie.filter;

import com.localgenie.Dagger2.ActivityScoped;
import dagger.Module;
import dagger.Provides;
import com.pojo.FilteredPriceResponse;

/**
 * <h>FilterDaggerResponseModule</h>
 * Created by Ali on 2/2/2018.
 */

@Module
public class FilterDaggerResponseModule
{
    @Provides
    @ActivityScoped
    FilteredPriceResponse provideFilteredPrice()
    {
        return new FilteredPriceResponse();
    }

}
