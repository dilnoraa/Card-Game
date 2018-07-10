package com.game.protocol;

import com.common.Card;

import java.io.Serializable;


public final class StartRoundRequest implements Serializable {
    private final int actionRoundDuration;
    private final long actionRoundStartTimestamp;
    private final long roundId;
    private final Card baseCard;
     private final int clientNo;

    public StartRoundRequest(int actionRoundDuration, long actionRoundStartTimestamp, long roundId, Card baseCard, int clientNo) {
        this.actionRoundDuration = actionRoundDuration;
        this.actionRoundStartTimestamp = actionRoundStartTimestamp;
        this.roundId = roundId;
        this.baseCard = baseCard;
        this.clientNo=clientNo;
    }

    public int getActionRoundDuration() {
        return actionRoundDuration;
    }
    
     public int getclientNo() {
        return clientNo;
    }

    public long getActionRoundStartTimestamp() {
        return actionRoundStartTimestamp;
    }

    public long getRoundId() {
        return roundId;
    }

    public Card getBaseCard() {
        return baseCard;
    }

    @Override
    public String toString() {
        return "StartRoundRequest{" +
                "actionRoundDuration=" + actionRoundDuration +
                ", actionRoundStartTimestamp=" + actionRoundStartTimestamp +
                ", roundId=" + roundId +
                ", baseCard=" + baseCard +
                '}';
    }
}
