package blackjack.domain;

import blackjack.domain.card.Card;
import blackjack.domain.card.CardDeck;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dealer {

    private final String name;
    private List<Card> cards;

    public Dealer(String name, List<Card> cards) {
        cards = new ArrayList<>(cards);
        validate(cards);

        this.name = name;
        this.cards = cards;
    }

    private void validate(List<Card> cards) {
        Objects.requireNonNull(cards, "[ERROR] 카드는 null일 수 없습니다.");
        validateSize(cards);
        validateDistinct(cards);
    }

    private void validateSize(List<Card> cards) {
        if (cards.size() != 2) {
            throw new IllegalArgumentException("[ERROR] 카드를 두 장 받고 시작해야 합니다.");
        }
    }

    private void validateDistinct(List<Card> cards) {
        if (cards.stream().distinct().count() != cards.size()) {
            throw new IllegalArgumentException("[ERROR] 카드는 중복될 수 없습니다.");
        }
    }

    public void play(CardDeck deck) {
        while (getCardsSum() < 17) {
            cards.add(deck.draw());
        }
    }

    public int getCardsSum() {
        return cards.stream()
                .mapToInt(Card::getValue)
                .sum();
    }

    public List<Card> getCards() {
        return cards;
    }
}