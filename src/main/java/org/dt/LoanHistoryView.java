package org.dt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dt.domain.DeviceUser;
import org.dt.domain.Loan;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.Switch;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class LoanHistoryView extends NavigationView implements Component,
        ClickListener {

    JPAContainer<Loan> container;
    private Table table = new Table();

    private Button filterButton = new Button("Filter", this);
    private Button chartButton = new Button("Chart", this);
    private Button refreshButton = new Button("Refresh", this);

    private LoanContainerFilterEditor filterEditor = new LoanContainerFilterEditor();

    public LoanHistoryView() {
        setCaption("Loans");
        buildView();
    }

    private void buildView() {
        if (container == null) {
            HorizontalButtonGroup horizontalButtonGroup = new HorizontalButtonGroup();
            horizontalButtonGroup.addComponents(filterButton, chartButton);
            setLeftComponent(horizontalButtonGroup);
            setRightComponent(refreshButton);
            container = DeviceTracker.get().createLoanContainer();
            container.setApplyFiltersImmediately(false);
            table.setContainerDataSource(container);
            final DateFormat df = SimpleDateFormat.getDateInstance();
            table.addGeneratedColumn("startDate", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    Date d = (Date) source.getItem(itemId)
                            .getItemProperty("startDate").getValue();
                    return df.format(d);
                }
            });
            table.addGeneratedColumn("endDate", new ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    Date d = (Date) source.getItem(itemId)
                            .getItemProperty("endDate").getValue();
                    return d == null ? "active" : df.format(d);
                }
            });
            table.setColumnHeader("endDate", "returned");

            table.setVisibleColumns(new Object[] { "startDate", "device",
                    "loaner", "endDate" });
            table.setSizeFull();
            setContent(table);
        }
    }

    @Override
    public void attach() {
        super.attach();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Button button = event.getButton();
        if (button == refreshButton) {
            container.refresh();
        } else if (button == filterButton) {
            filterEditor.showRelativeTo(filterButton);
        } else if (button == chartButton) {
            new ChartView(container, chartButton);
        }
    }

    public class LoanContainerFilterEditor extends Popover implements
            ValueChangeListener {

        private NativeSelect userSelect = new NativeSelect("User");
        private Switch activeOnly = new Switch("Active only");

        public LoanContainerFilterEditor() {
            setWidth("300px");
            NavigationView navigationView = new NavigationView("Filters");
            navigationView.setHeight("200px");
            VerticalComponentGroup verticalComponentGroup = new VerticalComponentGroup();

            userSelect.setNullSelectionAllowed(true);
            userSelect.addValueChangeListener(this);
            JPAContainer<DeviceUser> userContainer = DeviceTracker.get()
                    .getUserContainer();
            userSelect.setContainerDataSource(userContainer);
            userSelect.setImmediate(true);
            userSelect.setItemCaptionPropertyId("name");
            verticalComponentGroup.addComponent(userSelect);
            
            activeOnly.addValueChangeListener(this);
            activeOnly.setImmediate(true);
            verticalComponentGroup.addComponent(activeOnly);
            
            navigationView.setContent(verticalComponentGroup);
            setContent(navigationView);
        }

        @Override
        public void attach() {
            super.attach();
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            if (userSelect == event.getProperty()) {
                Object value = userSelect.getValue();
                DeviceUser entity = null;
                if (value != null) {
                    JPAContainerItem<DeviceUser> item = (JPAContainerItem<DeviceUser>) userSelect
                            .getItem(value);
                    entity = item.getEntity();
                }
                userFilter = entity;
            } else if( activeOnly == event.getProperty()) {
                activeOnlyFilter = activeOnly.getValue();
            }
            applyFilters();
        }

    }

    private DeviceUser userFilter;
    private boolean activeOnlyFilter;

    public void filter(DeviceUser user) {
        filterEditor.userSelect.setValue(user.getId());
    }

    private void applyFilters() {
        container.removeAllContainerFilters();
        if (userFilter != null) {
            container.addContainerFilter(new Equal("loaner", userFilter));
        }
        if(activeOnlyFilter) {
            Filter endNull = new IsNull("endDate");
            container.addContainerFilter(endNull);
        }
        container.applyFilters();
    }

}
