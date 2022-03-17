package blackjack.domain.participant;

import blackjack.domain.card.Cards;

public class Dealer extends Participant {

    private static final int HIT_STANDARD = 17;

    public Dealer(Cards cards) {
        super(new Name("딜러"), cards);
    }

    @Override
    public boolean isHittable() {
        return cards.isLessScoreThan(HIT_STANDARD);
    }
}
