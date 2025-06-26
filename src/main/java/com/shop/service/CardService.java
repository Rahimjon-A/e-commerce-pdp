
package com.shop.service;

import com.shop.model.Card;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter

public class CardService {

    private static final String CARD_FILE = "./files/cards.json";

    private final List<Card> cards;

    public CardService() {
        cards = FileUtility.loadFileFromJson(CARD_FILE, Card.class);
    }

    public void addCard(Card card) {
        cards.add(card);
        FileUtility.saveFileToJson(CARD_FILE, cards);
    }

    public List<Card> getCardsByUserId(UUID id) {
        List<Card> res = new ArrayList<>();

        for (Card card : cards) {
            if(card.getUserId().equals(id) && !card.isOrder()) {
                res.add(card);
            }
        }
        return res;
    }

    public List<Card> getOrdersByUserId(UUID id){
        List<Card> res = new ArrayList<>();

        for (Card card : cards) {
            if (card.getUserId().equals(id) && card.isOrder()){
                res.add(card);
            }
        }
        return res;
    }

    public void update() {
        FileUtility.saveFileToJson(CARD_FILE, cards);
    }

}
