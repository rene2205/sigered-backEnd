package ec.fin.be.sigered.dto;

import java.util.List;

public class MenuDto {
	private String label;
	private String routerLink;
	private List<MenuDto> items;
	private boolean expanded;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getRouterLink() {
		return routerLink;
	}
	public void setRouterLink(String routerLink) {
		this.routerLink = routerLink;
	}
	public List<MenuDto> getItems() {
		return items;
	}
	public void setItems(List<MenuDto> items) {
		this.items = items;
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
}
