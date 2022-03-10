package blackjack.domain.participant;

import blackjack.domain.Name;
import blackjack.domain.Rule;
import blackjack.domain.card.Card;
import java.util.List;

public class Dealer extends Participant {

    private static final int HIT_STANDARD = 17;

    public Dealer(List<Card> cards) {
        super(new Name("딜러"), cards);
    }

    @Override
    public boolean isHittable() {
        return Rule.INSTANCE.calculateSum(getCards()) < HIT_STANDARD;
    }
}