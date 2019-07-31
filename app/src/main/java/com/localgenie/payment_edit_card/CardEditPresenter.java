package com.localgenie.payment_edit_card;

/**
 * @author Pramod
 * @since 31-01-2018.
 */

public interface CardEditPresenter {

    void deleteCard(String auth, String cardId);

    void makeDefault(String auth, String cardId);
}
