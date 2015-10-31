package me.laochen.dao.core;

/**
 * @author simple
 *
 */
public class Criteria {
	public String fields="*";
	public String concondition="";
	public String orderBy;
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getConcondition() {
		return concondition;
	}
	public void setConcondition(String concondition) {
		this.concondition = concondition;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public String groupBy;
	public Integer offset=0;
	public Integer limit = 15;
	public String[] params;
	
	
}
