package com.localgenie.payment_details;

/**
 * @author Pramod
 * @since 31-01-2018.
 */

public interface PaymentDetailPresenter {

    void addCard(String auth, String email, String card_token);
}
