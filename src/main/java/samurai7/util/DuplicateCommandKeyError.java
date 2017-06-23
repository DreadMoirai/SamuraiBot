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
package samurai7.util;

import org.apache.commons.lang3.reflect.TypeUtils;
import samurai7.core.Command;
import samurai7.core.IModule;

public class DuplicateCommandKeyError extends Error {
    public DuplicateCommandKeyError(String key, Class<? extends Command> existing, Class<? extends Command> duplicate) {
        super("Key \"" + key + "\" for Command " + duplicate.getSimpleName() + " in Module " + TypeUtils.getTypeArguments(duplicate, Command.class).get(Command.class.getTypeParameters()[0]).getTypeName() + " is already mapped to Command " + existing.getSimpleName() + " in Module " + TypeUtils.getTypeArguments(existing, Command.class).get(Command.class.getTypeParameters()[0]).getTypeName());
    }
}
