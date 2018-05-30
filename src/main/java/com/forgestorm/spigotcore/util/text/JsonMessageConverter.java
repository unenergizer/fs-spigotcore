package com.forgestorm.spigotcore.util.text;


import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic class for conversion of strings to JsonMessages.
 *
 * Original:
 * https://github.com/Janmm14/JsonMessageMaker/blob/master/src/main/java/de/janmm14/jsonmessagemaker/api/JsonMessageConverter.java
 *
 * EDIT: unenergizer edited some of this class to simplify colors {@link Text} and to add a new opt case. Removed some lombok annotations.
 * As per the MIT license, I will do my very best to document all changes I have made in comments.
 *
 * @author Janmm14
 */
public final class JsonMessageConverter {

    private static final Pattern CAM_PATTERN = Pattern.compile("\\[jmc\\|(.+?)\\](.+?)\\[\\/jmc\\]", Pattern.CASE_INSENSITIVE);
    private static final Pattern ARG_SPLIT_PATTERN = Pattern.compile("\\|jmc\\|", Pattern.CASE_INSENSITIVE);

    public BaseComponent[] convert(@NonNull final String input) {
        return convert(input, null, null);
    }

    /**
     * Converts strings to a json message using net.md-5:bungeecord-chat and uses the set options
     * Example Message:
     * player.spigot().sendMessage(messageConverter.convert("[jmc|hover=runs /list|jmc|run=/list]run /list[/jmc]"));
     *
     * @param input the input string, the format of the conversion parts is available in README.md
     * @param uniqueKey The unique command key to use when generating a command for cmsg.
     * @param npcName The name of the NPC as defined in the citizens config file.
     * @return the converted message as {@link BaseComponent}
     */
    @NonNull
    public BaseComponent[] convert(final String input, final String uniqueKey, final String npcName) {
        // Any text wrapped in Text.color(value) was not included in the original class.
        List<BaseComponent> components = new ArrayList<>();
        final Matcher matcher = CAM_PATTERN.matcher(input);
        int lastEnd = 0;
        while (matcher.find()) {
            final String argsStr = matcher.group(1);
            final String text = matcher.group(2);
            final String before = input.substring(lastEnd, matcher.start());
            components.addAll(Arrays.asList(TextComponent.fromLegacyText(Text.color(before))));
            final String[] args = ARG_SPLIT_PATTERN.split(argsStr);
            final TextComponent txt = new TextComponent(TextComponent.fromLegacyText(Text.color(text)));
            for (String arg : args) {
                final int i = arg.indexOf('=');
                final String opt = arg.substring(0, i).toLowerCase();
                final String val = arg.substring(i + 1, arg.length());

                switch (opt) {
                    case "hover":
                        txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Text.color(val))));
                        break;
                    case "suggest":
                        txt.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, val));
                        break;
                    case "run":
                        txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, val));
                        break;
                    case "cmsg": // Not found in original class.
                        txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Text.color("&eClick me!"))));
                        txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cmsg " + uniqueKey + " " + npcName + " " + val));
                        break;
                    case "link":
                        txt.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, val));
                        break;
                }
            }
            components.add(txt);

            lastEnd = matcher.end();
        }
        if (lastEnd < (input.length() - 1)) {
            final String after = input.substring(lastEnd, input.length());
            components.addAll(Arrays.asList(TextComponent.fromLegacyText(Text.color(after))));
        }
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return components.toArray(new BaseComponent[components.size()]);
    }
}
