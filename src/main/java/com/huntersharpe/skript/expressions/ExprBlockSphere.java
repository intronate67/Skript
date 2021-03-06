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

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.SimpleExpression;
import com.huntersharpe.skript.util.BlockSphereIterator;

import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import ch.njol.util.coll.iterator.IteratorIterable;

/**
 * @author Peter Güttinger
 */
@Name("Block Sphere")
@Description("All blocks in a sphere around a center, mostly useful for looping.")
@Examples("loop blocks in radius 5 around the player:")
@Since("1.0")
public class ExprBlockSphere extends SimpleExpression<Block> {
	static {
		Skript.registerExpression(ExprBlockSphere.class, Block.class, ExpressionType.COMBINED,
				"(all|the|) blocks in radius %number% [(of|around) %location%]",
				"(all|the|) blocks around %location% in radius %number%");
	}
	
	@SuppressWarnings("null")
	private Expression<Number> radius;
	@SuppressWarnings("null")
	private Expression<Location> center;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		radius = (Expression<Number>) exprs[matchedPattern];
		center = (Expression<Location>) exprs[1 - matchedPattern];
		return true;
	}
	
	@Override
	public Iterator<Block> iterator(final Event e) {
		final Location l = center.getSingle(e);
		final Number r = radius.getSingle(e);
		if (l == null || r == null)
			return new EmptyIterator<Block>();
		return new BlockSphereIterator(l, r.doubleValue());
	}
	
	@Override
	@Nullable
	protected Block[] get(final Event e) {
		final Number r = radius.getSingle(e);
		if (r == null)
			return new Block[0];
		final ArrayList<Block> list = new ArrayList<Block>((int) (1.1 * 4 / 3. * Math.PI * Math.pow(r.doubleValue(), 3)));
		for (final Block b : new IteratorIterable<Block>(iterator(e)))
			list.add(b);
		return list.toArray(new Block[list.size()]);
	}
	
	@Override
	public Class<? extends Block> getReturnType() {
		return Block.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the blocks in radius " + radius + " around " + center.toString(e, debug);
	}
	
	@Override
	public boolean isLoopOf(final String s) {
		return s.equalsIgnoreCase("block");
	}
	
	@Override
	public boolean isSingle() {
		return false;
	}
	
}
