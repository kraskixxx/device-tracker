package org.dt;

import org.dt.domain.DeviceUser;

import com.vaadin.addon.touchkit.extensions.TouchKitIcon;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TabSheet.Tab;

public class MainView extends TabBarView {

    private LoanHistoryView loanHistoryView = new LoanHistoryView();

    public MainView() {
        Tab t = addTab(new DeviceListView());
        // t.setIcon(new ThemeResource("../dt/device-icon.png"));
        TouchKitIcon.tablet.addTo(t);
        t = addTab(loanHistoryView);
        // t.setIcon(new ThemeResource("../dt/loans-icon.png"));
        TouchKitIcon.book.addTo(t);
        t = addTab(new MapView());
        TouchKitIcon.globe.addTo(t);
        // t.setIcon(new ThemeResource("../dt/world-icon.png"));
        t = addTab(new MyDetailsView());
        // t.setIcon(new ThemeResource("../dt/user-icon.png"));
        TouchKitIcon.user.addTo(t);
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
