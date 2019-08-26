package com.localgenie.biddingFlow;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ali on 6/12/2018.
 */
public class AnswerHashMap {


  private String answer,question,questionId,cardId,promoCode;
  private LatLng latLng;
  private int paymentType;
  private boolean isWallet;
  private int questionType;

    public AnswerHashMap(String answer, String question, String questionId, int paymentType, String cardId, boolean isWallet, String promoCode, LatLng latLng, int questionType) {
        this.answer = answer;
        this.question = question;
        this.questionId = questionId;
        this.paymentType = paymentType;
        this.cardId = cardId;
        this.isWallet = isWallet;
        this.promoCode = promoCode;
        this.latLng = latLng;
        this.questionType = questionType;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
