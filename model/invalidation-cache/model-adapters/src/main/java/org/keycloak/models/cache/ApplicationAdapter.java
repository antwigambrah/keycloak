package org.keycloak.models.cache;

import org.keycloak.models.ApplicationModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.cache.entities.CachedApplication;
import org.keycloak.models.cache.entities.CachedClient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationAdapter extends ClientAdapter implements ApplicationModel {
    protected ApplicationModel updated;
    protected CachedApplication cached;

    public ApplicationAdapter(RealmModel cachedRealm, CachedApplication cached, CacheKeycloakSession cacheSession, KeycloakCache cache) {
        super(cachedRealm, cached, cache, cacheSession);
        this.cached = cached;
    }

    @Override
    protected void getDelegateForUpdate() {
        if (updated == null) {
            updatedClient = updated = cacheSession.getDelegate().getApplicationById(getId(), cachedRealm);
            if (updated == null) throw new IllegalStateException("Not found in database");
        }
    }

    @Override
    public void updateApplication() {
        if (updated != null) updated.updateApplication();
    }

    @Override
    public String getName() {
        return getClientId();
    }

    @Override
    public String getClientId() {
        return getName();
    }

    @Override
    public void setName(String name) {
        getDelegateForUpdate();
        updated.setName(name);
    }

    @Override
    public boolean isSurrogateAuthRequired() {
        if (updated != null) return updated.isSurrogateAuthRequired();
        return cached.isSurrogateAuthRequired();
    }

    @Override
    public void setSurrogateAuthRequired(boolean surrogateAuthRequired) {
        getDelegateForUpdate();
        updated.setSurrogateAuthRequired(surrogateAuthRequired);
    }

    @Override
    public String getManagementUrl() {
        if (updated != null) return updated.getManagementUrl();
        return cached.getManagementUrl();
    }

    @Override
    public void setManagementUrl(String url) {
        getDelegateForUpdate();
        updated.setManagementUrl(url);
    }

    @Override
    public String getBaseUrl() {
        if (updated != null) return updated.getBaseUrl();
        return cached.getBaseUrl();
    }

    @Override
    public void setBaseUrl(String url) {
        getDelegateForUpdate();
        updated.setBaseUrl(url);
    }

    @Override
    public List<String> getDefaultRoles() {
        if (updated != null) return updated.getDefaultRoles();
        return cached.getDefaultRoles();
    }

    @Override
    public void addDefaultRole(String name) {
        getDelegateForUpdate();
        updated.addDefaultRole(name);
    }

    @Override
    public void updateDefaultRoles(String[] defaultRoles) {
        getDelegateForUpdate();
        updated.updateDefaultRoles(defaultRoles);
    }

    @Override
    public Set<RoleModel> getApplicationScopeMappings(ClientModel client) {
        Set<RoleModel> roleMappings = client.getScopeMappings();

        Set<RoleModel> appRoles = new HashSet<RoleModel>();
        for (RoleModel role : roleMappings) {
            RoleContainerModel container = role.getContainer();
            if (container instanceof RealmModel) {
            } else {
                ApplicationModel app = (ApplicationModel)container;
                if (app.getId().equals(getId())) {
                    appRoles.add(role);
                }
            }
        }

        return appRoles;
    }

    @Override
    public boolean isBearerOnly() {
        if (updated != null) return updated.isBearerOnly();
        return cached.isBearerOnly();
    }

    @Override
    public void setBearerOnly(boolean only) {
        getDelegateForUpdate();
        updated.setBearerOnly(only);
    }

    @Override
    public RoleModel getRole(String name) {
        if (updated != null) return updated.getRole(name);
        String id = cached.getRoles().get(name);
        if (id == null) return null;
        return cacheSession.getRoleById(id, cachedRealm);
    }

    @Override
    public RoleModel addRole(String name) {
        getDelegateForUpdate();
        return updated.addRole(name);
    }

    @Override
    public RoleModel addRole(String id, String name) {
        getDelegateForUpdate();
        return updated.addRole(id, name);
    }

    @Override
    public boolean removeRole(RoleModel role) {
        getDelegateForUpdate();
        return updated.removeRole(role);
    }

    @Override
    public Set<RoleModel> getRoles() {
        if (updated != null) return updated.getRoles();

        Set<RoleModel> roles = new HashSet<RoleModel>();
        for (String id : cached.getRoles().values()) {
            roles.add(cacheSession.getRoleById(id, cachedRealm));
        }
        return roles;
    }
}
