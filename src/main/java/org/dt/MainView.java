package org.dt;

import org.dt.domain.DeviceUser;

import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.TabSheet.Tab;

public class MainView extends TabBarView {

    private LoanHistoryView loanHistoryView = new LoanHistoryView();

    public MainView() {
        Tab t = addTab(new DeviceListView());
        t.setIcon(FontAwesome.TABLET);
        t = addTab(loanHistoryView);
        t.setIcon(FontAwesome.BOOK);
        t = addTab(new MapView());
        t.setIcon(FontAwesome.GLOBE);
        t = addTab(new MyDetailsView());
        t.setIcon(FontAwesome.USER);
    }

    /**
     * Active LoanHistoryView, with given user as filter
     * 
     * @param user
     */
    public void loanView(DeviceUser user) {
        loanHistoryView.filter(user);
        setSelectedTab(loanHistoryView);
    }

}
