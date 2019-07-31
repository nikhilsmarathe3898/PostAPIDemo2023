package com.localgenie.invoice;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Ali on 10/18/2018.
 */
@Module
public interface InvoiceDaggerModule {

    @Binds
    @ActivityScoped
    InvoiceModel.InvoiceView provideView(InvoiceActivity invoiceActivity);

    @Binds
    @ActivityScoped
    InvoiceModel.InvoicePre providePresenter(InvoicePresenter invoicePresenter);
}
