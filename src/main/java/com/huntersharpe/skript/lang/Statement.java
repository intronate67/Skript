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

package com.huntersharpe.skript.lang;

import java.util.Iterator;

import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.lang.function.EffFunctionCall;
import com.huntersharpe.skript.log.ParseLogHandler;
import com.huntersharpe.skript.log.SkriptLogger;

/**
 * Supertype of conditions and effects
 * 
 * @author Peter Güttinger
 * @see Condition
 * @see Effect
 */
public abstract class Statement extends TriggerItem implements SyntaxElement {
	
	@SuppressWarnings({"rawtypes", "unchecked", "null"})
	@Nullable
	public static Statement parse(final String s, final String defaultError) {
		final ParseLogHandler log = SkriptLogger.startParseLogHandler();
		try {
			final EffFunctionCall f = EffFunctionCall.parse(s);
			if (f != null) {
				log.printLog();
				return f;
			} else if (log.hasError()) {
				log.printError();
				return null;
			} else {
				log.printError();
			}
		} finally {
			log.stop();
		}
		return (Statement) SkriptParser.parse(s, (Iterator) Skript.getStatements().iterator(), defaultError);
	}
	
}
