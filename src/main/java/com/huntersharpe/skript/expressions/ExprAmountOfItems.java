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

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.aliases.ItemType;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.SimpleExpression;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Amount of Items")
@Description("Counts how many of a particular <a href='../classes/#itemtype'>item type</a> are in a given inventory.")
@Examples("message \"You have %number of ores in the player's inventory% ores in your inventory.\"")
@Since("2.0")
public class ExprAmountOfItems extends SimpleExpression<Integer> {
	static {
		Skript.registerExpression(ExprAmountOfItems.class, Integer.class, ExpressionType.PROPERTY, "[the] (amount|number) of %itemtypes% (in|of) %inventories%");
	}
	
	@SuppressWarnings("null")
	private Expression<ItemType> items;
	@SuppressWarnings("null")
	private Expression<Inventory> invis;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		items = (Expression<ItemType>) exprs[0];
		invis = (Expression<Inventory>) exprs[1];
		return true;
	}
	
	@Override
	protected Integer[] get(final Event e) {
		int r = 0;
		final ItemType[] types = items.getArray(e);
		for (final Inventory invi : invis.getArray(e)) {
			itemsLoop: for (final ItemStack i : invi.getContents()) {
				for (final ItemType t : types) {
					if (t.isOfType(i)) {
						r += i == null ? 1 : i.getAmount();
						continue itemsLoop;
					}
				}
			}
		}
		return new Integer[] {r};
	}
	
	@Override
	public Integer[] getAll(final Event e) {
		int r = 0;
		final ItemType[] types = items.getAll(e);
		for (final Inventory invi : invis.getAll(e)) {
			itemsLoop: for (final ItemStack i : invi.getContents()) {
				for (final ItemType t : types) {
					if (t.isOfType(i)) {
						r += i == null ? 1 : i.getAmount();
						continue itemsLoop;
					}
				}
			}
		}
		return new Integer[] {r};
	}
	
	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "number of " + items + " in " + invis;
	}
}
