
package com.shop.service;

import com.shop.model.Card;
import com.shop.utility.FileUtility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
        return cards.stream().filter(card -> card.getUserId().equals(id) && !card.isOrder()).toList();
    }

    public List<Card> getOrdersByUserId(UUID id) {
        return cards.stream().filter(card -> card.getUserId().equals(id) && card.isOrder()).toList();

    }

    public void update() {
        FileUtility.saveFileToJson(CARD_FILE, cards);
    }

}
