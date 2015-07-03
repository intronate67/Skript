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

package com.huntersharpe.skript.hooks.regions.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.hooks.regions.RegionsPlugin;
import com.huntersharpe.skript.lang.Condition;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.util.Direction;

import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Can Build")
@Description({"Tests whether a player is allowed to build at a certain location.",
		"This condition requires a supported <a href='../classes/#region'>regions</a> plugin to be installed."})
@Examples({"command /setblock <material>:",
		"	description: set the block at your crosshair to a different type",
		"	trigger:",
		"		player cannot build at the targeted block:",
		"			message \"You do not have permission to change blocks there!\"",
		"			stop",
		"		set the targeted block to argument"})
@Since("2.0")
public class CondCanBuild extends Condition {
	static {
		Skript.registerCondition(CondCanBuild.class,
				"%players% (can|(is|are) allowed to) build %directions% %locations%",
				"%players% (can('t|not)|(is|are)(n't| not) allowed to) build %directions% %locations%");
	}
	
	@SuppressWarnings("null")
	private Expression<Player> players;
	@SuppressWarnings("null")
	Expression<Location> locations;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		locations = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
		setNegated(matchedPattern == 1);
		return true;
	}
	
	@Override
	public boolean check(final Event e) {
		return players.check(e, new Checker<Player>() {
			@Override
			public boolean check(final Player p) {
				return locations.check(e, new Checker<Location>() {
					@Override
					public boolean check(final Location l) {
						return RegionsPlugin.canBuild(p, l);
					}
				}, isNegated());
			}
		});
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return players.toString(e, debug) + " can build " + locations.toString(e, debug);
	}
	
}
