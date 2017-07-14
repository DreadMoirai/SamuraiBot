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
import com.github.breadmoirai.samurai7.core.response.Response;
import com.github.breadmoirai.samurai7.core.response.Responses;
import com.github.breadmoirai.samurai.modules.music.MusicModule;

@Key({"volume", "volume+", "volume-", "vol", "vol+", "vol-"})
public class Volume extends ModuleCommand<MusicModule> {

    @Override
    public Response execute(CommandEvent event, MusicModule module) {
        int value = 0;
        if (event.isNumeric()) {
            value = Math.max(0, Math.min(150, Integer.parseInt(event.getContent())));
        }
        final long id = event.getGuildId();
        final int oldVol = module.getVolume(id);
        final String key = event.getKey();

        if (key.endsWith("+")) {
            if (!event.hasContent()) {
                final int newVol = Math.min(150, oldVol + 10);
                module.updateVolume(id, newVol);
                return Responses.ofFormat("Volume changed. `%02d` -> `%02d`", oldVol, newVol);
            } else {
                final int newVol = Math.min(150, oldVol + value);
                if (value != 0) module.updateVolume(id, newVol);
                return Responses.ofFormat("Volume changed. `%02d` -> `%02d`", oldVol, newVol);
            }
        } else if (key.endsWith("-")) {
            if (!event.hasContent()) {
                final int newVol = Math.max(0, oldVol - 10);
                module.updateVolume(id, newVol);
                return Responses.ofFormat("Volume changed. `%02d` -> `%02d`", oldVol, newVol);
            } else {
                final int newVol = Math.max(0, oldVol - value);
                if (value != 0) module.updateVolume(id, newVol);
                return Responses.ofFormat("Volume changed. `%02d` -> `%02d`", oldVol, newVol);
            }
        } else {
            if (!event.hasContent()) {
                return Responses.ofFormat("Volume is currently set at `%02d`", oldVol);
            } else {
                if (value != oldVol) module.updateVolume(id, value);
                return Responses.ofFormat("Volume changed. `%02d` -> `%02d`", oldVol, value);
            }
        }
    }
}
