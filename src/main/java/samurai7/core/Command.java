/*
 *       Copyright 2017 Ton Ly
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
package samurai7.core;

import org.apache.commons.lang3.reflect.TypeUtils;
import samurai7.core.response.Response;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public abstract class Command<M extends IModule> implements ICommand{

    private M module;
    private ICommandEvent event;

    @Override
    public final Optional<Response> call() {
        final Response r = execute(getEvent(), module);
        //noinspection Duplicates
        if (r != null) {
            if (r.getAuthorId() == 0) r.setAuthorId(getEvent().getAuthorId());
            if (r.getChannelId() == 0) r.setChannelId(getEvent().getChannelId());
            if (r.getGuildId() == 0) r.setGuildId(getEvent().getGuildId());
        }
        return Optional.ofNullable(r);
    }

    protected abstract Response execute(ICommandEvent event, M module);

    @Override
    final public ICommandEvent getEvent() {
        return event;
    }

    @Override
    final public void setModules(Map<Type, IModule> moduleTypeMap) {
        //noinspection unchecked
        this.module = (M) moduleTypeMap.get(TypeUtils.getTypeArguments(this.getClass(), Command.class).get(Command.class.getTypeParameters()[0]));
    }

    @Override
    final public void setEvent(ICommandEvent event) {
        this.event = event;
    }
}