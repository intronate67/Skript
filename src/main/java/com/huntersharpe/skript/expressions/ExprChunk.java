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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.classes.Converter;
import com.huntersharpe.skript.classes.Changer.ChangeMode;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.PropertyExpression;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.util.Direction;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Chunk")
@Description("The <a href='../classes/#chunk'>chunk</a> a block, location or entity is in")
@Examples("add the chunk at the player to {protected chunks::*}")
@Since("2.0")
public class ExprChunk extends PropertyExpression<Location, Chunk> {
	
	static {
		Skript.registerExpression(ExprChunk.class, Chunk.class, ExpressionType.PROPERTY, "[the] chunk[s] (of|%-directions%) %locations%", "%locations%'[s] chunk[s]");
	}
	
	@SuppressWarnings("null")
	private Expression<Location> locations;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		if (matchedPattern == 0) {
			locations = (Expression<Location>) exprs[1];
			if (exprs[0] != null)
				locations = Direction.combine((Expression<? extends Direction>) exprs[0], locations);
		} else {
			locations = (Expression<Location>) exprs[0];
		}
		setExpr(locations);
		return true;
	}
	
	@Override
	protected Chunk[] get(final Event e, final Location[] source) {
		return get(source, new Converter<Location, Chunk>() {
			@SuppressWarnings("null")
			@Override
			public Chunk convert(final Location l) {
				return l.getChunk();
			}
		});
	}
	
	@Override
	public Class<? extends Chunk> getReturnType() {
		return Chunk.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the chunk at " + locations.toString(e, debug);
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.RESET)
			return new Class[0];
		return null;
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) {
		assert mode == ChangeMode.RESET;
		
		final Chunk[] cs = getArray(e);
		for (final Chunk c : cs)
			c.getWorld().regenerateChunk(c.getX(), c.getZ());
	}
	
}
