/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.github.breadmoirai.samurai.modules.music.commands;

import com.github.breadmoirai.samurai7.core.CommandEvent;
import com.github.breadmoirai.samurai7.core.command.Key;
import com.github.breadmoirai.samurai7.core.command.ModuleCommand;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai7.core.response.simple.BasicResponse;
import com.github.breadmoirai.samurai.modules.music.GuildMusicManager;
import com.github.breadmoirai.samurai.modules.music.MusicModule;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Key("history")
public class History extends ModuleCommand<MusicModule> {

    @Override
    public BasicResponse execute(CommandEvent event, MusicModule module) {
        final Optional<GuildMusicManager> managerOptional = module.retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildMusicManager manager = managerOptional.get();
            final Deque<AudioTrack> history = manager.getScheduler().getHistory();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            final AtomicInteger i = new AtomicInteger(0);
            boolean hyperLink = event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
            sb.append("**History**");
            history.stream().limit(10).map(track -> MusicModule.trackInfoDisplay(track, true, hyperLink)).map(s -> String.format("\n`%d.` %s", i.incrementAndGet(), s)).forEachOrdered(sb::append);
            return Responses.of(eb.build());
        }
        return null;
    }
}
