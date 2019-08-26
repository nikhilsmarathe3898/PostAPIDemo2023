package com.localgenie.model.card;

public class DeleteCard
{
    private String cardId;

    public DeleteCard(String cardId) {
        super();
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
