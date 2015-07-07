/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011-2014 Peter Güttinger
 * 
 */

package com.huntersharpe.skript.expressions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.world.SpawnChangeEvent;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.classes.Converter;
import com.huntersharpe.skript.classes.Changer.ChangeMode;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.effects.Delay;
import com.huntersharpe.skript.expressions.base.PropertyExpression;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;

import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author Peter Güttinger
 */
@Name("Spawn")
@Description("The spawnpoint of a world.")
@Examples({"teleport all players to spawn",
		"set the spawn point of \"world\" to the player's location"})
@Since("1.4.2")
public class ExprSpawn extends PropertyExpression<World, Location> {
	static {
		Skript.registerExpression(ExprSpawn.class, Location.class, ExpressionType.PROPERTY, "[the] spawn[s] [(point|location)[s]] [of %worlds%]", "%worlds%'[s] spawn[s] [(point|location)[s]]");
	}
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends World>) exprs[0]);
		return true;
	}
	
	@Override
	protected Location[] get(final Event e, final World[] source) {
		if (getTime() == -1 && e instanceof SpawnChangeEvent && !Delay.isDelayed(e)) {
			return new Location[] {((SpawnChangeEvent) e).getPreviousLocation()};
		}
		return get(source, new Converter<World, Location>() {
			@Override
			@Nullable
			public Location convert(final World w) {
				return w.getSpawnLocation();
			}
		});
	}
	
	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "spawn of " + getExpr().toString(e, debug);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Location.class);
		return null;
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		assert mode == ChangeMode.SET;
		assert delta != null;
		
		final Location l = (Location) delta[0];
		final int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
		for (final World w : getExpr().getArray(e)) {
			w.setSpawnLocation(x, y, z);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean setTime(final int time) {
		return super.setTime(time, getExpr(), SpawnChangeEvent.class);
	}
	
}