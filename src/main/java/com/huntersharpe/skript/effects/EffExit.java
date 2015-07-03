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

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.ScriptLoader;
import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Conditional;
import com.huntersharpe.skript.lang.Effect;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.Loop;
import com.huntersharpe.skript.lang.TriggerItem;
import com.huntersharpe.skript.lang.TriggerSection;
import com.huntersharpe.skript.lang.While;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.log.ErrorQuality;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Exit")
@Description("Exits a given amount of loops and conditionals, or the entire trigger.")
@Examples({"if player has any ore:",
		"	stop",
		"message \"%player% has no ores!\"",
		"loop blocks above the player:",
		"	loop-block is not air:",
		"		exit 2 sections",
		"	set loop-block to water"})
@Since("")
public class EffExit extends Effect { // TODO [code style] warn user about code after a stop effect
	static {
		Skript.registerEffect(EffExit.class,
				"(exit|stop) [trigger]",
				"(exit|stop) [(1|a|the|this)] (0¦section|1¦loop|2¦conditional)",
				"(exit|stop) <\\d+> (0¦section|1¦loop|2¦conditional)s",
				"(exit|stop) all (0¦section|1¦loop|2¦conditional)s");
	}
	
	private int breakLevels;
	
	private final static int EVERYTHING = 0, LOOPS = 1, CONDITIONALS = 2;
	private final static String[] names = {"sections", "loops", "conditionals"};
	private int type;
	
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		switch (matchedPattern) {
			case 0:
				breakLevels = ScriptLoader.currentSections.size() + 1;
				type = EVERYTHING;
				break;
			case 1:
			case 2:
				breakLevels = matchedPattern == 1 ? 1 : Integer.parseInt(parser.regexes.get(0).group());
				type = parser.mark;
				if (breakLevels > numLevels(type)) {
					if (numLevels(type) == 0)
						Skript.error("can't stop any " + names[type] + " as there are no " + names[type] + " present", ErrorQuality.SEMANTIC_ERROR);
					else
						Skript.error("can't stop " + breakLevels + " " + names[type] + " as there are only " + numLevels(type) + " " + names[type] + " present", ErrorQuality.SEMANTIC_ERROR);
					return false;
				}
				break;
			case 3:
				type = parser.mark;
				breakLevels = numLevels(type);
				if (breakLevels == 0) {
					Skript.error("can't stop any " + names[type] + " as there are no " + names[type] + " present", ErrorQuality.SEMANTIC_ERROR);
					return false;
				}
				break;
		}
		return true;
	}
	
	private final static int numLevels(final int type) {
		if (type == EVERYTHING)
			return ScriptLoader.currentSections.size();
		int r = 0;
		for (final TriggerSection s : ScriptLoader.currentSections) {
			if (type == CONDITIONALS ? s instanceof Conditional : s instanceof Loop || s instanceof While)
				r++;
		}
		return r;
	}
	
	@Override
	@Nullable
	protected TriggerItem walk(final Event e) {
		debug(e, false);
		TriggerItem n = this;
		for (int i = breakLevels; i > 0;) {
			n = n.getParent();
			if (n == null) {
				assert false : this;
				return null;
			}
			if (type == EVERYTHING || type == CONDITIONALS && n instanceof Conditional || type == LOOPS && (n instanceof Loop || n instanceof While))
				i--;
		}
		return n instanceof Loop ? ((Loop) n).getActualNext() : n instanceof While ? ((While) n).getActualNext() : n.getNext();
	}
	
	@Override
	protected void execute(final Event e) {
		assert false;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "stop " + breakLevels + " " + names[type];
	}
	
}
