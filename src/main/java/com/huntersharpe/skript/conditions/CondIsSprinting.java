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
@Name("Is Sprinting")
@Description("Checks whether a player is sprinting")
@Examples("player is not sprinting")
@Since("1.4.4")
public class CondIsSprinting extends PropertyCondition<Player> {
	static {
		register(CondIsSprinting.class, "sprinting", "players");
	}
	
	@Override
	public boolean check(final Player p) {
		return p.isSprinting();
	}
	
	@Override
	protected String getPropertyName() {
		return "sprinting";
	}
	
}
