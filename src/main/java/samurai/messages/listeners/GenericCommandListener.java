package samurai.messages.listeners;

import samurai.command.basic.GenericCommand;

/**
 * @author TonTL
 * @version 3/15/2017
 */
public interface GenericCommandListener extends SamuraiListener {
    void onCommand(GenericCommand command);
}
