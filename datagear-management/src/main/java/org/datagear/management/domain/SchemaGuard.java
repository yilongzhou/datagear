/*
 * Copyright 2018-2023 datagear.tech
 *
 * This file is part of DataGear.
 *
 * DataGear is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * DataGear is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with DataGear.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.datagear.management.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.datagear.analysis.support.JsonSupport;
import org.datagear.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 数据源防护。
 * 
 * @author datagear@163.com
 *
 */
public class SchemaGuard extends AbstractStringIdEntity implements CloneableEntity
{
	private static final long serialVersionUID = 1L;

	/** 连接URL匹配模式 */
	private String pattern;

	/** 连接用户名匹配模式 */
	private String userPattern = null;

	/** 连接属性匹配模式 */
	private List<SchemaPropertyPattern> propertyPatterns = null;

	/** 是否允许：true 允许；false 禁止 */
	private boolean permitted = true;

	/** 优先级 */
	private int priority = 0;

	/** 是否启用 */
	private boolean enabled = true;

	/** 创建时间 */
	private Date createTime = new Date();

	public SchemaGuard()
	{
		super();
	}

	public SchemaGuard(String id)
	{
		super(id);
	}

	public SchemaGuard(String id, String pattern)
	{
		super(id);
		this.pattern = pattern;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	@Nullable
	public String getUserPattern()
	{
		return userPattern;
	}

	public void setUserPattern(String userPattern)
	{
		this.userPattern = userPattern;
	}

	@Nullable
	public List<SchemaPropertyPattern> getPropertyPatterns()
	{
		return propertyPatterns;
	}

	public void setPropertyPatterns(List<SchemaPropertyPattern> propertyPatterns)
	{
		this.propertyPatterns = propertyPatterns;
	}

	public boolean isPermitted()
	{
		return permitted;
	}

	public void setPermitted(boolean permitted)
	{
		this.permitted = permitted;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	/**
	 * 返回{@linkplain #getPropertyPatterns()}的JSON。
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getPropertyPatternsJson()
	{
		if (this.propertyPatterns == null)
			return "[]";

		return JsonSupport.generate(this.propertyPatterns, "[]");
	}

	/**
	 * 设置{@linkplain #setPropertyPatterns(List)}的JSON。
	 * 
	 * @param json
	 */
	public void setPropertyPatternsJson(String json)
	{
		if (StringUtil.isEmpty(json))
			return;

		SchemaPropertyPattern[] propertyPatterns = JsonSupport.parse(json, SchemaPropertyPattern[].class, null);
		setPropertyPatterns(Arrays.asList(propertyPatterns));
	}

	@Override
	public SchemaGuard clone()
	{
		SchemaGuard entity = new SchemaGuard();
		BeanUtils.copyProperties(this, entity);

		return entity;
	}

	/**
	 * 将{@linkplain SchemaGuard}列表按照优先级排序，{@linkplain SchemaGuard#getPriority()}越大越靠前、{@linkplain SchemaGuard#getCreateTime()}越新越靠前。
	 * 
	 * @param schemaGuards
	 */
	public static void sortByPriority(List<? extends SchemaGuard> schemaGuards)
	{
		if (schemaGuards == null)
			return;

		Collections.sort(schemaGuards, SCHEMA_GUARD_PRIORITY_COMPARATOR);
	}

	private static final Comparator<SchemaGuard> SCHEMA_GUARD_PRIORITY_COMPARATOR = new Comparator<SchemaGuard>()
	{
		@Override
		public int compare(SchemaGuard o1, SchemaGuard o2)
		{
			// 优先级越高越靠前
			int p = Integer.valueOf(o2.priority).compareTo(o1.priority);

			if (p == 0)
			{
				// 越新创建越靠前

				Date o1d = o1.createTime;
				Date o2d = o2.createTime;

				if (o1d == null && o2d == null)
					return 0;
				else if (o1d == null)
					return 1;
				else if (o2d == null)
					return -1;
				else
					return o2d.compareTo(o1d);
			}
			else
				return p;
		}
	};
}
