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

package com.huntersharpe.skript.conditions;

import org.bukkit.entity.Player;

import com.huntersharpe.skript.conditions.base.PropertyCondition;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;

/**
 * @author Peter Güttinger
 */
@Name("Is Flying")
@Description("Checks whether a player is flying")
@Examples("player is not flying")
@Since("1.4.4")
public class CondIsFlying extends PropertyCondition<Player> {
	
	static {
		register(CondIsFlying.class, "flying", "players");
	}
	
	@Override
	public boolean check(final Player p) {
		return p.isFlying();
	}
	
	@Override
	protected String getPropertyName() {
		return "flying";
	}
	
}
