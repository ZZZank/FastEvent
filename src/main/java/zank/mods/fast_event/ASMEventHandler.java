/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package zank.mods.fast_event;

import lombok.val;
import net.minecraftforge.eventbus.api.*;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.HashMap;

import static org.objectweb.asm.Type.getMethodDescriptor;

public class ASMEventHandler implements IEventListener
{
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final HashMap<Method, IEventListener> cache = new HashMap<>();

    private final IEventListener handler;
    private final SubscribeEvent subInfo;
    private final String readable;

    public ASMEventHandler(Object target, Method method, boolean isGeneric) {
        val rawHandler = cache.computeIfAbsent(
            method,
            m -> EventListenerFactory.createRawListener(LOOKUP, m, target)
        );

        subInfo = method.getAnnotation(SubscribeEvent.class);
        readable = "ASM: " + target + " " + method.getName() + getMethodDescriptor(method);

        Type filter = isGeneric ? extractGenericType(method) : null;
        if (filter != null) {
            handler = (event) -> {
                if (filter == ((IGenericEvent<?>) event).getGenericType()) {
                    rawHandler.invoke(event);
                }
            };
        } else {
            handler = rawHandler;
        }
    }

    private static @Nullable Type extractGenericType(Method method) {
        val type = method.getGenericParameterTypes()[0];
        Type filter = null;
        if (type instanceof ParameterizedType) {
            filter = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (filter instanceof ParameterizedType) {
                // Unlikely that nested generics will ever be relevant for event filtering, so discard them
                filter = ((ParameterizedType) filter).getRawType();
            } else if (filter instanceof WildcardType) {
                // If there's a wildcard filter of Object.class, then remove the filter.
                final WildcardType wildcardType = (WildcardType) filter;
                if (wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0] == Object.class
                    && wildcardType.getLowerBounds().length == 0) {
                    filter = null;
                }
            }
        }
        return filter;
    }

    @Override
    public void invoke(Event event)
    {
        if (!event.isCancelable() || !event.isCanceled() || subInfo.receiveCanceled()) {
            handler.invoke(event);
        }
    }

    public EventPriority getPriority()
    {
        return subInfo.priority();
    }

    public String toString()
    {
        return readable;
    }
}
