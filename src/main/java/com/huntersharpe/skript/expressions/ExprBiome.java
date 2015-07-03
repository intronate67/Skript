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
import org.bukkit.block.Biome;
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
@Name("Biome")
@Description("The biome at a certain location. Please note that biomes are only defined for x/z-columns, i.e. the <a href='#ExprAltitude'>altitude</a> (y-coordinate) doesn't matter. ")
@Examples({"# damage player in deserts constantly",
		"every real minute:",
		"	loop all players:",
		"		biome at loop-player is desert",
		"		damage the loop-player by 1"})
@Since("1.4.4")
public class ExprBiome extends PropertyExpression<Location, Biome> {
	static {
		Skript.registerExpression(ExprBiome.class, Biome.class, ExpressionType.PROPERTY, "[the] biome (of|%direction%) %location%", "%location%'[s] biome");
	}
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr(matchedPattern == 1 ? (Expression<? extends Location>) exprs[0] : Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]));
		return true;
	}
	
	@Override
	protected Biome[] get(final Event e, final Location[] source) {
		return get(source, new Converter<Location, Biome>() {
			@SuppressWarnings("null")
			@Override
			public Biome convert(final Location l) {
				return l.getWorld().getBiome(l.getBlockX(), l.getBlockZ());
			}
		});
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return new Class[] {Biome.class};
		return super.acceptChange(mode);
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			assert delta != null;
			for (final Location l : getExpr().getArray(e))
				l.getWorld().setBiome(l.getBlockX(), l.getBlockZ(), (Biome) delta[0]);
		} else {
			super.change(e, delta, mode);
		}
	}
	
	@Override
	public Class<? extends Biome> getReturnType() {
		return Biome.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the biome at " + getExpr().toString(e, debug);
	}
	
}
