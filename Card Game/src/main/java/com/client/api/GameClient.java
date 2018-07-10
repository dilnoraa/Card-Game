package com.client.api;

import com.game.protocol.FinishRoundRequest;
import com.game.protocol.StartRoundRequest;

/**
 *  This ia a game client that will receive messages from server
 */
public interface GameClient {
    /**
     * Server notifies client that round is started
     */
    void  startRound(StartRoundRequest startRoundRequest);

    /**
     * Server notifies client that round is finished
     */
    void finishRound(FinishRoundRequest finishRoundRequest);
}
