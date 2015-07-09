package com.anpi.app.config;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.PropertyProjection;

/**
 * A custom 'Group By HAVING' projection for use with Hibernate Criteria.
 * 
 */
public class GroupByHavingProjection extends PropertyProjection
{
	// ========================= CONSTANTS =================================

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3316795021430206470L;

	// ========================= FIELDS ====================================

	/**
	 * The HAVING projection
	 */
	private final Projection projection;

	/**
	 * The property to group by
	 */
	private final String groupByProperty;

	/**
	 * The optional operator
	 */
	private String op;

	/**
	 * The optional value
	 */
	private Object value;

	// ========================= CONSTRUCTORS ==============================

	/**
	 * A group by having projection
	 * 
	 * @param groupByProperty
	 *            the group by property
	 * @param projection
	 *            the having projection
	 */
	public GroupByHavingProjection(final String groupByProperty,
			final Projection projection)
	{
		this(groupByProperty, projection, null, null);
	}

	/**
	 * A group by having projection with an operator and value
	 * 
	 * @param groupByProperty
	 *            the group by property
	 * @param projection
	 *            the having projection
	 * @param op
	 *            an operator to apply to the having
	 * @param value
	 *            the value of a having based on the operator
	 */
	public GroupByHavingProjection(final String groupByProperty,
			final Projection projection, final String op, final Object value)
	{
		super(groupByProperty, true);
		this.projection = projection;
		this.groupByProperty = groupByProperty;
		this.op = op;
		this.value = value;
	}

	// ========================= IMPL: PropertyProjection ==============================

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.criterion.PropertyProjection#toGroupSqlString(org.hibernate.Criteria,
	 * org.hibernate.criterion.CriteriaQuery)
	 */
	@Override
	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException
	{
		final StringBuffer sb = new StringBuffer(50);

		sb.append(criteriaQuery.getColumn(criteria, groupByProperty));
		sb.append(" having ");
		sb.append("(");

		final String proj = projection.toSqlString(criteria, (int) 0xCAFEBABE,
				criteriaQuery);

		// Remove the alias
		sb.append(proj.substring(0, proj.indexOf("as")).trim());

		sb.append(")");

		if (value != null)
		{
			sb.append(" ");
			sb.append(op);
			sb.append(" ");
			sb.append(value);
		}

		return sb.toString();
	}

}