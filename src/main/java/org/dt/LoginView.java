package org.dt;

import java.util.List;

import org.dt.domain.DeviceUser;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.CssLayout;

public class LoginView extends NavigationView implements PositionCallback {

    private NewUserView newUserView;

    public LoginView() {
        setCaption("DeviceTracker: Login");

        CssLayout layout = new CssLayout();
        setContent(layout);

        VerticalComponentGroup existingUserList = new VerticalComponentGroup();
        existingUserList.setCaption("Login as existing user:");
        layout.addComponent(existingUserList);
        List<DeviceUser> users2 = DeviceTracker.get().getUsers();
        for (final DeviceUser deviceUser : users2) {
            NavigationButton navigationButton = new NavigationButton();
            navigationButton.setCaption(deviceUser.getName());
            navigationButton
                    .addClickListener(new NavigationButtonClickListener() {
                        @Override
                        public void buttonClick(NavigationButtonClickEvent event) {
                            DeviceTracker.get().login(deviceUser);
                        }
                    });
            existingUserList.addComponent(navigationButton);
        }
        newUserView = new NewUserView();
        layout.addComponent(newUserView);
    }
    
    @Override
    public void attach() {
        super.attach();
        Geolocator.detect(this);
    }

    @Override
    public void onSuccess(Position position) {
        DeviceUser user;
        boolean loggedIn = getParent() == null;
        if(loggedIn) {
            user = DeviceTracker.get().getUser();
        } else {
            user = newUserView.getUser();
        }
        user.setLat(position.getLatitude());
        user.setLon(position.getLongitude());
        if(loggedIn) {
            DeviceTracker.get().saveUser();
        }
        
    }

    @Override
    public void onFailure(int errorCode) {
        // TODO Auto-generated method stub
        
    }

}
