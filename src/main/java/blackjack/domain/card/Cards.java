package blackjack.domain.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Cards {

    private static final int INITIAL_CARDS_SIZE = 2;
    private static final int BLACKJACK_SCORE = 21;

    private final List<Card> cards;

    public Cards(List<Card> cards) {
        cards = new ArrayList<>(cards);
        validateCards(cards);
        this.cards = cards;
    }

    private void validateCards(List<Card> cards) {
        validateNull(cards);
        validateSize(cards);
        validateDistinct(cards);
    }

    private void validateNull(List<Card> cards) {
        Objects.requireNonNull(cards, "[ERROR] 카드는 null일 수 없습니다.");
    }

    private void validateSize(List<Card> cards) {
        if (cards.size() != INITIAL_CARDS_SIZE) {
            throw new IllegalArgumentException("[ERROR] 카드 " + INITIAL_CARDS_SIZE + "장이 필요합니다.");
        }
    }

    private void validateDistinct(List<Card> cards) {
        if (cards.stream().distinct().count() != cards.size()) {
            throw new IllegalArgumentException("[ERROR] 카드는 중복될 수 없습니다.");
        }
    }

    public void add(Card card) {
        validateDistinct(card);
        cards.add(card);
    }

    private void validateDistinct(Card card) {
        if (cards.contains(card)) {
            throw new IllegalArgumentException("[ERROR] 카드는 중복될 수 없습니다.");
        }
    }

    public List<Card> getFrontCards(int cardSize) {
        return IntStream.range(0, cardSize)
            .boxed()
            .map(cards::get)
            .collect(Collectors.toList());
    }

    public int calculateScore() {
        int aceCount = calculateAceCount();
        int minimumSum = calculateMinimumSum();
        return calculateSum(aceCount, minimumSum);
    }

    private int calculateSum(int aceCount, int minimumSum) {
        return IntStream.rangeClosed(0, aceCount)
            .map(i -> minimumSum + (aceCount - i) * 10)
            .filter(i -> i <= BLACKJACK_SCORE)
            .findFirst()
            .orElse(minimumSum);
    }

    private int calculateMinimumSum() {
        return cards.stream()
            .mapToInt(Card::getValue)
            .sum();
    }

    private int calculateAceCount() {
        return (int)cards.stream()
            .filter(card -> card.isSameValueWith(Denomination.ACE))
            .count();
    }

    public boolean isSameSize(int size) {
        return cards.size() == size;
    }

    public boolean isOverScoreThan(int score) {
        return calculateScore() > score;
    }

    public boolean isLessScoreThan(int score) {
        return calculateScore() < score;
    }

    public boolean isSameScore(int score) {
        return calculateScore() == score;
    }

    public List<Card> getValues() {
        return List.copyOf(cards);
    }
}
