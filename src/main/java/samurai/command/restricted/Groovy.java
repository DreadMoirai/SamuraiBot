package samurai.command.restricted;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.codec.binary.Hex;
import samurai.Bot;
import samurai.audio.YoutubeAPI;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.database.DatabaseSingleton;
import samurai.files.SamuraiStore;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author TonTL
 * @version 5.0
 * @since 2/18/2017
 */
@Key({"groovy"})
@Admin
@Creator
public class Groovy extends Command {

    private static final Binding BINDING;
    private static final GroovyShell GROOVY_SHELL;

    static {
        BINDING = new Binding();
        BINDING.setVariable("CREATOR", "DreadMoirai");
        BINDING.setVariable("BOT", Bot.class);
        BINDING.setVariable("STORE", SamuraiStore.class);
        BINDING.setVariable("CF", CommandFactory.class);
        BINDING.setVariable("DB", DatabaseSingleton.class);
        BINDING.setVariable("YT", YoutubeAPI.class);
        GROOVY_SHELL = new GroovyShell(BINDING);

    }

    public static void addBinding(String name, Object value) {
        BINDING.setVariable(name, value);
    }

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final String content = context.getContent().replaceAll("`", "");
        if (content.length() <= 1) return null;
        BINDING.setVariable("context", context);
        if (content.contains("binding")) {
            final Set set = BINDING.getVariables().entrySet();
            if (set.toArray() instanceof Map.Entry[]) {
                Map.Entry[] entryArray = (Map.Entry[]) set.toArray();
                return FixedMessage.build(Arrays.stream(entryArray).map(entry -> entry.getKey().toString() + '=' + entry.getValue().getClass().getSimpleName()).collect(Collectors.joining("\n")));
            }
        }

        try {
            Object result = GROOVY_SHELL.evaluate(content);
            if (result != null) {
                if (result instanceof byte[]) {
                    if (((byte[]) result).length == 0) {
                        return FixedMessage.build("Empty byte array");
                    }
                    return FixedMessage.build(Hex.encodeHexString((byte[]) result));
                }
                return FixedMessage.build(result.toString());
            } else return FixedMessage.build("Null");
        } catch (Exception e) {
            return FixedMessage.build(e.getMessage());
        }
    }

}
