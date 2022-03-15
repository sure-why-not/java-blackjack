package blackjack.domain;

import static blackjack.domain.Judgement.*;
import static blackjack.domain.card.Denomination.*;
import static blackjack.domain.card.Pattern.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import blackjack.domain.card.Card;
import blackjack.domain.card.Cards;
import blackjack.domain.card.Denomination;
import blackjack.domain.participant.Dealer;
import blackjack.domain.participant.Name;
import blackjack.domain.participant.Player;

public class WinResultTest {

    @ParameterizedTest
    @MethodSource("provideResultForNotBust")
    @DisplayName("딜러와 플레이어 둘 다 버스트하지 않았을 경우 점수가 더 큰 쪽이 이긴다.")
    void bothNotBust(Dealer dealer, Map<Judgement, Integer> judgementMap, Judgement playerJudgement) {
        // given
        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, NINE);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(playerJudgement)
        );
    }

    private static Stream<Arguments> provideResultForNotBust() {
        return Stream.of(
            Arguments.of(createDealer(TEN), createJudgementMap(1, 0, 0), LOSE),
            Arguments.of(createDealer(NINE), createJudgementMap(0, 1, 0), DRAW),
            Arguments.of(createDealer(EIGHT), createJudgementMap(0, 0, 1), WIN)
        );
    }

    @ParameterizedTest
    @MethodSource("provideForPlayerBust")
    @DisplayName("플레이어가 버스트면 무조건 딜러가 이긴다.")
    void playerBust(Card dealerCard, Card playerCard) {
        // given
        Dealer dealer = createDealer(SIX);
        dealer.hit(dealerCard);

        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, TEN);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        player.hit(playerCard);
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();
        Map<Judgement, Integer> judgementMap = createJudgementMap(1, 0, 0);

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(LOSE)
        );
    }

    private static Stream<Arguments> provideForPlayerBust() {
        return Stream.of(
            Arguments.of(new Card(HEART, SIX), new Card(HEART, TWO)),
            Arguments.of(new Card(HEART, FIVE), new Card(HEART, TWO))
        );
    }

    @Test
    @DisplayName("플레이어가 버스트가 아니고 딜러가 버스트면 플레이어가 이긴다.")
    void dealerBust() {
        // given
        Dealer dealer = createDealer(SIX);
        dealer.hit(new Card(HEART, SIX));

        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, TEN);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();
        Map<Judgement, Integer> judgementMap = createJudgementMap(0, 0, 1);

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(WIN)
        );
    }

    @Test
    @DisplayName("똑같이 21점이어도 딜러만 블랙잭이면 딜러가 이긴다.")
    void blackJackOnlyDealer() {
        // given
        Dealer dealer = createDealer(ACE);

        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, TEN);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        player.hit(new Card(HEART, ACE));
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();
        Map<Judgement, Integer> judgementMap = createJudgementMap(1, 0, 0);

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(LOSE)
        );
    }

    @Test
    @DisplayName("블랙잭 끼리는 비긴다.")
    void blackJackDrawWithBlackJack() {
        // given
        Dealer dealer = createDealer(ACE);

        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, ACE);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();
        Map<Judgement, Integer> judgementMap = createJudgementMap(0, 1, 0);

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(DRAW)
        );
    }

    @Test
    @DisplayName("똑같이 21점이어도 플래이어만 블랙잭이면 플레이어가 블랙잭이다.")
    void blackJackOnlyPlayer() {
        // given
        Dealer dealer = createDealer(TEN);
        dealer.hit(new Card(HEART, ACE));

        Card heartTen = new Card(HEART, TEN);
        Card spadeNine = new Card(SPADE, ACE);
        Cards playerCards = new Cards(List.of(heartTen, spadeNine));
        Money betMoney = new Money(1000);
        Player player = new Player(new Name("pobi"), playerCards, betMoney);
        List<Player> players = List.of(player);

        // when
        WinResult winResult = new WinResult(dealer, players);
        Map<Judgement, Integer> dealerResult = winResult.getDealerResult();
        Map<String, Judgement> playersResult = winResult.getPlayersResult();
        Map<Judgement, Integer> judgementMap = createJudgementMap(0, 0, 1);

        // then
        assertAll(
            () -> assertThat(dealerResult).isEqualTo(judgementMap),
            () -> assertThat(playersResult.get(player.getName())).isEqualTo(BLACKJACK)
        );
    }

    private static Dealer createDealer(Denomination denomination2) {
        Card card1 = new Card(DIAMOND, TEN);
        Card card2 = new Card(CLOVER, denomination2);
        Cards cards = new Cards(List.of(card1, card2));
        return new Dealer(cards);
    }

    private static Map<Judgement, Integer> createJudgementMap(int win, int draw, int lose) {
        Map<Judgement, Integer> judgementMap = new EnumMap<>(Judgement.class);
        if (win > 0) {
            judgementMap.put(WIN, win);
        }
        if (draw > 0) {
            judgementMap.put(DRAW, draw);
        }
        if (lose > 0) {
            judgementMap.put(LOSE, lose);
        }
        return judgementMap;
    }
}
