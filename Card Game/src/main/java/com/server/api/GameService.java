package com.server.api;

import com.game.protocol.PlayerActionRequest;
import com.game.protocol.PlayerActionResponse;

/**
 * This is a game server that will receive messages from client
 */
public interface GameService {
    /**
     * Client notifies server of its action
     */
    boolean playerAction(PlayerActionRequest playerActionRequest);

    /**
     * Client manually sets a base card. To be used only for testing purpose!
     * Should only be called outside active game round and return an error if game round is already active.
     */
   //  SetBaseCardResponse setBaseCard(SetBaseCardRequest setBaseCardRequest);
}
