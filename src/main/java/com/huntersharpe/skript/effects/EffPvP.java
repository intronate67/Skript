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

package com.huntersharpe.skript.effects;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Effect;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("PvP")
@Description("Set the PvP status for a given world.")
@Examples({"enable PvP #(current world only)",
		"disable PvP in all worlds"})
@Since("1.3.4")
public class EffPvP extends Effect {
	
	static {
		Skript.registerEffect(EffPvP.class, "enable PvP [in %worlds%]", "disable PVP [in %worlds%]");
	}
	
	@SuppressWarnings("null")
	private Expression<World> worlds;
	private boolean enable;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		worlds = (Expression<World>) exprs[0];
		enable = matchedPattern == 0;
		return true;
	}
	
	@Override
	protected void execute(final Event e) {
		for (final World w : worlds.getArray(e)) {
			w.setPVP(enable);
		}
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (enable ? "enable" : "disable") + " PvP in " + worlds.toString(e, debug);
	}
	
}
