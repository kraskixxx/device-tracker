package org.dt;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.dt.domain.AbstractEntity;
import org.dt.domain.Device;
import org.dt.domain.DeviceGroup;
import org.dt.domain.DeviceUser;
import org.dt.domain.Loan;
import org.dt.domain.Loan_;
import org.dt.domain.Location;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * The UI's "main" class
 */
@Widgetset("org.dt.gwt.AppWidgetSet")
@Theme("touchkit")
@Push
public class DeviceTracker extends UI {

    private static final String PERSISTENCE_UNIT_NAME = "org.dt.domain";

    private static EntityManagerFactory factory;

    static {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        EntityManager m = factory.createEntityManager();

        CriteriaBuilder qb = m.getCriteriaBuilder();
        
        CriteriaQuery<DeviceUser> c = qb.createQuery(DeviceUser.class);
        List<DeviceUser> r = m.createQuery(c).getResultList();
        if (r.isEmpty()) {

            m.getTransaction().begin();

            // Add some test data
            DeviceUser deviceUser = new DeviceUser();
            deviceUser.setName("Matti");
            deviceUser.setEmail("matti@vaadin.com");
            m.persist(deviceUser);

            DeviceGroup ios = new DeviceGroup();
            ios.setName("iOS");
            m.persist(ios);

            DeviceGroup android = new DeviceGroup();
            android.setName("Android");
            m.persist(android);

            DeviceGroup wp = new DeviceGroup();
            wp.setName("Windows 8");
            m.persist(wp);

            Location hq = new Location();
            hq.setName("Vaadin HQ");
            hq.setLat(60.45252);
            hq.setLon(22.301259);
            m.persist(hq);

            Device device = new Device();
            device.setName("iPad Mini");
            device.setHomeLocation(hq);
            device.setDeviceGroup(ios);
            m.persist(device);

            device = new Device();
            device.setName("iPad");
            device.setHomeLocation(hq);
            device.setDeviceGroup(ios);
            m.persist(device);

            device = new Device();
            device.setName("Nexus 7");
            device.setHomeLocation(hq);
            device.setDeviceGroup(android);
            m.persist(device);

            device = new Device();
            device.setName("Nokia Lumia 900");
            device.setHomeLocation(hq);
            device.setDeviceGroup(wp);
            m.persist(device);

            Location usa = new Location();
            usa.setName("Vaadin USA");
            usa.setLat(37.257159);
            usa.setLon(-121.779617);
            m.persist(usa);

            device = new Device();
            device.setName("iPad");
            device.setHomeLocation(usa);
            device.setDeviceGroup(ios);
            m.persist(device);

            device = new Device();
            device.setName("Nexus 7");
            device.setHomeLocation(usa);
            device.setDeviceGroup(android);
            m.persist(device);

            Location germany = new Location();
            germany.setName("Vaadin Germany");
            germany.setLat(49.922935);
            germany.setLon(8.745117);
            m.persist(germany);

            device = new Device();
            device.setName("iPad");
            device.setHomeLocation(germany);
            device.setDeviceGroup(ios);
            m.persist(device);

            device = new Device();
            device.setName("Nexus 7");
            device.setHomeLocation(germany);
            device.setDeviceGroup(android);
            m.persist(device);

            m.getTransaction().commit();
        }
        m.close();
    }

    private EntityManager em;

    public DeviceTracker() {
        em = factory.createEntityManager();
    }

    @Override
    public void detach() {
        super.detach();
        em.close();
    }

    private DeviceUser user;

    @Override
    protected void init(VaadinRequest request) {
        if (getUser() == null) {
            setContent(new LoginView());
        } else {
            setContent(new MainView());
        }
    }

    public static DeviceTracker get() {
        return (DeviceTracker) UI.getCurrent();
    }

    public DeviceUser getUser() {
        return user;
    }

    public void login(DeviceUser user) {
        if (user.getId() == null) {
            // persist
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }
        setUser(user);
        setContent(new MainView());
    }

    private void setUser(DeviceUser user) {
        this.user = user;
    }

    public List<DeviceUser> getUsers() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<DeviceUser> c = qb.createQuery(DeviceUser.class);
        return em.createQuery(c).getResultList();
    }

    public List<Location> getLocations() {
        em.clear(); // force new entities
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Location> c = qb.createQuery(Location.class);
        return em.createQuery(c).getResultList();
    }

    public List<DeviceGroup> getDeviceGroups() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<DeviceGroup> c = qb.createQuery(DeviceGroup.class);
        return em.createQuery(c).getResultList();
    }

    public List<Device> getDevices() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Device> c = qb.createQuery(Device.class);
        return em.createQuery(c).getResultList();
    }

    public void loan(Device device) {
        Loan loan = new Loan();
        loan.setDevice(device);
        loan.setLoaner(getUser());
        em.getTransaction().begin();
        em.persist(loan);
        em.merge(device);
        em.getTransaction().commit();
    }

    public void endLoan(Device device) {
        Loan loan = device.getLoan();
        loan.setEndDate(new Date());
        device.setLoan(null);
        em.getTransaction().begin();
        em.merge(loan);
        em.merge(device);
        em.getTransaction().commit();
    }

    public void saveUser() {
        user = saveOrPersist(user);
    }

    public void refreshUser() {
        user = refresh(user);
    }

    public <T extends AbstractEntity> T saveOrPersist(T e) {
        em.getTransaction().begin();
        if (e.getId() == null) {
            em.persist(e);
        } else {
            e = em.merge(e);
        }
        em.getTransaction().commit();
        return (T) e;
    }

    public JPAContainer<Loan> createLoanContainer() {
        JPAContainer<Loan> make = JPAContainerFactory.make(Loan.class, em);
        make.getEntityProvider().setEntitiesDetached(false);
        return make;
    }

    public JPAContainer<Location> getLocationsContainer() {
        JPAContainer<Location> make = JPAContainerFactory.make(Location.class,
                em);
        make.getEntityProvider().setEntitiesDetached(false);
        return make;
    }

    public JPAContainer<DeviceGroup> getDeviceGroupContainer() {
        JPAContainer<DeviceGroup> make = JPAContainerFactory.make(
                DeviceGroup.class, em);
        make.getEntityProvider().setEntitiesDetached(false);
        return make;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractEntity> T refresh(T location) {
        if (em.contains(location)) {
            em.refresh(location);
        } else if (location.getId() != null) {
            location = (T) em.find(location.getClass(), location.getId());
        }
        return (T) location;
    }

    public JPAContainer<DeviceUser> getUserContainer() {
        JPAContainer<DeviceUser> make = JPAContainerFactory.make(
                DeviceUser.class, em);
        make.getEntityProvider().setEntitiesDetached(false);
        return make;
    }

    public List<Loan> getMyActiveLoans() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Loan> c = qb.createQuery(Loan.class);
        Root<Loan> p = c.from(Loan.class);
        Predicate condition = qb.and(qb.equal(p.get(Loan_.loaner), getUser()),
                qb.isNull(p.get(Loan_.endDate)));
        c.where(condition);
        return em.createQuery(c).getResultList();
    }
}