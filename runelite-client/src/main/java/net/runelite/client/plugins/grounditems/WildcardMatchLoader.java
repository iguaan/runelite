/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.grounditems;

import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

class WildcardMatchLoader extends CacheLoader<String, Boolean>
{
	// Regex used for matching item names with others
	private static final Pattern WILDCARD_PATTERN = Pattern.compile("(?i)[^*]+|(\\*)");

	private final List<String> nameFilters;

	WildcardMatchLoader(List<String> nameFilters)
	{
		this.nameFilters = nameFilters;
	}

	@Override
	public Boolean load(@Nonnull final String key)
	{
		if (Strings.isNullOrEmpty(key))
		{
			return false;
		}

		final String filteredName = key.trim();

		for (final String filter : nameFilters)
		{
			final Matcher matcher = WILDCARD_PATTERN.matcher(filter);
			final StringBuffer buffer = new StringBuffer();

			buffer.append("(?i)");
			while (matcher.find())
			{
				if (matcher.group(1) != null)
				{
					matcher.appendReplacement(buffer, ".*");
				}
				else
				{
					matcher.appendReplacement(buffer, "\\\\Q" + matcher.group(0) + "\\\\E");
				}
			}

			matcher.appendTail(buffer);
			final String replaced = buffer.toString();

			if (filteredName.matches(replaced))
			{
				return true;
			}
		}

		return false;
	}
}
