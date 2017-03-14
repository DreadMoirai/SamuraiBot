package samurai.command.general;

import samurai.Bot;
import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.data.SamuraiStore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/23/2017
 */
@Key("flame")
@Source
public class Flame extends Command {

    private static Random r;

    static {
        r = new Random();
    }

    @Override
    protected SamuraiMessage buildMessage() {
        try {
            List<String> flameList = Files.readAllLines(Paths.get(SamuraiStore.class.getResource("./flame.txt").toURI()));
            if (mentions.isEmpty()) {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", author.getAsMention()));
            } else {
                return FixedMessage.build(flameList.get(r.nextInt(flameList.size())).replace("[victim]", mentions.get(0).getAsMention()));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Bot.logError(e);
            return null;
        }
    }
}