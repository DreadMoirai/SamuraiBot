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
import com.github.breadmoirai.samurai7.core.command.ModuleMultiCommand;
import com.github.breadmoirai.samurai7.core.response.Response;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai.modules.music.GuildMusicManager;
import com.github.breadmoirai.samurai.modules.music.MusicModule;

import java.util.Optional;

public class PausePlayback extends ModuleMultiCommand<MusicModule> {

    @Key("pause")
    public Response pause(CommandEvent event, MusicModule module) {
       if (setPaused(module, event.getGuildId(), true))
        return Responses.of("Playback Paused");
       return null;
    }

    @Key({"unpause", "resume"})
    public Response resume(CommandEvent event, MusicModule module) {
        if (setPaused(module, event.getGuildId(), false))
            return Responses.of("Playback Resumed");
        return null;
    }


    private boolean setPaused(MusicModule module, long guildId, boolean value) {
        Optional<GuildMusicManager> musicManager = module.retrieveManager(guildId);
        musicManager.ifPresent(guildMusicManager -> guildMusicManager.getPlayer().setPaused(value));
        return musicManager.isPresent();
    }

}