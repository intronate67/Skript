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

import java.lang.reflect.Array;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.ScriptLoader;
import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.classes.Converter;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.Loop;
import com.huntersharpe.skript.lang.Variable;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.ConvertedExpression;
import com.huntersharpe.skript.lang.util.SimpleExpression;
import com.huntersharpe.skript.log.ErrorQuality;
import com.huntersharpe.skript.registrations.Classes;
import com.huntersharpe.skript.registrations.Converters;
import com.huntersharpe.skript.util.Utils;

import ch.njol.util.Kleenean;

/**
 * Used to access a loop's current value.
 * <p>
 * TODO expression to get the current # of execution (e.g. loop-index/number/count/etc (not number though));
 * 
 * @author Peter Güttinger
 */
@Name("Loop value")
@Description("The currently looped value.")
@Examples({"# countdown:",
		"loop 10 times:",
		"	message \"%11 - loop-number%\"",
		"	wait a second",
		"# generate a 10x10 floor made of randomly coloured wool below the player:",
		"loop blocks from the block below the player to the block 10 east of the block below the player:",
		"	loop blocks from the loop-block to the block 10 north of the loop-block:",
		"		set loop-block-2 to any wool"})
@Since("1.0")
public class ExprLoopValue extends SimpleExpression<Object> {
	static {
		Skript.registerExpression(ExprLoopValue.class, Object.class, ExpressionType.SIMPLE, "[the] loop-<.+>");
	}
	
	@SuppressWarnings("null")
	private String name;
	
	@SuppressWarnings("null")
	private Loop loop;
	
	// whether this loops a variable
	boolean isVariableLoop = false;
	// if this loops a variable and isIndex is true, return the index of the variable instead of the value
	boolean isIndex = false;
	
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		name = parser.expr;
		String s = "" + parser.regexes.get(0).group();
		int i = -1;
		final Matcher m = Pattern.compile("^(.+)-(\\d+)$").matcher(s);
		if (m.matches()) {
			s = "" + m.group(1);
			i = Utils.parseInt("" + m.group(2));
		}
		final Class<?> c = Classes.getClassFromUserInput(s);
		int j = 1;
		Loop loop = null;
		for (final Loop l : ScriptLoader.currentLoops) {
			if ((c != null && c.isAssignableFrom(l.getLoopedExpression().getReturnType())) || "value".equals(s) || l.getLoopedExpression().isLoopOf(s)) {
				if (j < i) {
					j++;
					continue;
				}
				if (loop != null) {
					Skript.error("There are multiple loops that match loop-" + s + ". Use loop-" + s + "-1/2/3/etc. to specify which loop's value you want.", ErrorQuality.SEMANTIC_ERROR);
					return false;
				}
				loop = l;
				if (j == i)
					break;
			}
		}
		if (loop == null) {
			Skript.error("There's no loop that matches 'loop-" + s + "'", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if (loop.getLoopedExpression() instanceof Variable) {
			isVariableLoop = true;
			if (((Variable<?>) loop.getLoopedExpression()).isIndexLoop(s))
				isIndex = true;
		}
		this.loop = loop;
		return true;
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	protected <R> ConvertedExpression<Object, ? extends R> getConvertedExpr(final Class<R>... to) {
		if (isVariableLoop && !isIndex) {
			return new ConvertedExpression<Object, R>(this, (Class<R>) Utils.getSuperType(to), new Converter<Object, R>() {
				@Override
				@Nullable
				public R convert(final Object o) {
					return Converters.convert(o, to);
				}
			});
		} else {
			return super.getConvertedExpr(to);
		}
	}
	
	@Override
	public Class<? extends Object> getReturnType() {
		if (isIndex)
			return String.class;
		return loop.getLoopedExpression().getReturnType();
	}
	
	@Override
	@Nullable
	protected Object[] get(final Event e) {
		if (isVariableLoop) {
			@SuppressWarnings("unchecked")
			final Entry<String, Object> current = (Entry<String, Object>) loop.getCurrent(e);
			if (current == null)
				return null;
			if (isIndex)
				return new String[] {current.getKey()};
			final Object[] one = (Object[]) Array.newInstance(getReturnType(), 1);
			one[0] = current.getValue();
			return one;
		}
		final Object[] one = (Object[]) Array.newInstance(getReturnType(), 1);
		one[0] = loop.getCurrent(e);
		return one;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		if (e == null)
			return name;
		if (isVariableLoop) {
			@SuppressWarnings("unchecked")
			final Entry<String, Object> current = (Entry<String, Object>) loop.getCurrent(e);
			if (current == null)
				return Classes.getDebugMessage(null);
			return isIndex ? "\"" + current.getKey() + "\"" : Classes.getDebugMessage(current.getValue());
		}
		return Classes.getDebugMessage(loop.getCurrent(e));
	}
	
}
