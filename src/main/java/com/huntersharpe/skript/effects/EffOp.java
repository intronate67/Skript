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

import org.bukkit.OfflinePlayer;
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
@Name("op/deop")
@Description("Grant/revoke a user operator status.")
@Examples({"op the player",
		"deop all players"})
@Since("1.0")
public class EffOp extends Effect {
	
	static {
		Skript.registerEffect(EffOp.class, "[de[-]]op %offlineplayers%");
	}
	
	@SuppressWarnings("null")
	private Expression<OfflinePlayer> players;
	private boolean op;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		players = (Expression<OfflinePlayer>) exprs[0];
		op = !parseResult.expr.substring(0, 2).equalsIgnoreCase("de");
		return true;
	}
	
	@Override
	protected void execute(final Event e) {
		for (final OfflinePlayer p : players.getArray(e)) {
			p.setOp(op);
		}
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (op ? "" : "de") + "op " + players.toString(e, debug);
	}
	
}