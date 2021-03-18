package io.taptalk.TapTalk.Model;

public class TAPCountryRecycleItem {
    public enum RecyclerItemType {
        COUNTRY_INITIAL, COUNTRY_ITEM
    }

    private RecyclerItemType recyclerItemType;
    private TAPCountryListItem countryListItem;
    private char countryInitial;
    private boolean isSelected;

    public RecyclerItemType getRecyclerItemType() {
        return recyclerItemType;
    }

    public void setRecyclerItemType(RecyclerItemType recyclerItemType) {
        this.recyclerItemType = recyclerItemType;
    }

    public TAPCountryListItem getCountryListItem() {
        return countryListItem;
    }

    public void setCountryListItem(TAPCountryListItem countryListItem) {
        this.countryListItem = countryListItem;
    }

    public char getCountryInitial() {
        return countryInitial;
    }

    public void setCountryInitial(char countryInitial) {
        this.countryInitial = countryInitial;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
