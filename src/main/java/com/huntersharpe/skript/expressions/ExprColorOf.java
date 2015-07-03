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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.aliases.ItemType;
import com.huntersharpe.skript.classes.Changer.ChangeMode;
import com.huntersharpe.skript.classes.Changer.ChangerUtils;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.SimplePropertyExpression;
import com.huntersharpe.skript.util.Color;

import ch.njol.util.coll.CollectionUtils;

/**
 * @author Peter Güttinger
 */
@Name("Colour of")
@Description("The <a href='../classes/#color'>colour</a> of an item, can also be used to colour chat messages with \"&lt;%colour of ...%&gt;this text is coloured!\".")
@Examples({"on click on wool:",
		"	message \"This wool block is <%colour of block%>%colour of block%<reset>!\"",
		"	set the colour of the block to black"})
@Since("1.2")
public class ExprColorOf extends SimplePropertyExpression<Object, Color> {
	static {
		register(ExprColorOf.class, Color.class, "colo[u]r[s]", "itemstacks/entities");
	}
	
	@SuppressWarnings("null")
	@Override
	@Nullable
	public Color convert(final Object o) {
		if (o instanceof ItemStack || o instanceof Item) {
			final ItemStack is = o instanceof ItemStack ? (ItemStack) o : ((Item) o).getItemStack();
			final MaterialData d = is.getData();
			if (d instanceof Colorable)
				return Color.byWoolColor(((Colorable) d).getColor());
		} else if (o instanceof Colorable) { // Sheep
			return Color.byWoolColor(((Colorable) o).getColor());
		}
		return null;
	}
	
	@Override
	protected String getPropertyName() {
		return "colour";
	}
	
	@Override
	public Class<Color> getReturnType() {
		return Color.class;
	}
	
	boolean changeItemStack = false;
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode != ChangeMode.SET)
			return null;
		if (Entity.class.isAssignableFrom(getExpr().getReturnType()))
			return CollectionUtils.array(Color.class);
		if (!getExpr().isSingle())
			return null;
		if (ChangerUtils.acceptsChange(getExpr(), mode, ItemStack.class, ItemType.class)) {
			changeItemStack = ChangerUtils.acceptsChange(getExpr(), mode, ItemStack.class);
			return CollectionUtils.array(Color.class);
		}
		return null;
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		assert mode == ChangeMode.SET;
		assert delta != null;
		
		final Color c = (Color) delta[0];
		final Object[] os = getExpr().getArray(e);
		if (os.length == 0)
			return;
		
		for (final Object o : os) {
			if (o instanceof ItemStack || o instanceof Item) {
				final ItemStack is = o instanceof ItemStack ? (ItemStack) o : ((Item) o).getItemStack();
				final MaterialData d = is.getData();
				if (d instanceof Colorable)
					((Colorable) d).setColor(c.getWoolColor());
				else
					continue;
				
				if (o instanceof ItemStack) {
					if (changeItemStack)
						getExpr().change(e, new ItemStack[] {is}, mode);
					else
						getExpr().change(e, new ItemType[] {new ItemType(is)}, mode);
				} else {
					((Item) o).setItemStack(is);
				}
			} else if (o instanceof Colorable) {
				((Colorable) o).setColor(c.getWoolColor());
			}
		}
	}
	
}
