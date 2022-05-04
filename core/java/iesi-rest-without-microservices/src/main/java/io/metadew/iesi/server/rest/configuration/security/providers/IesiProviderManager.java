package io.metadew.iesi.server.rest.configuration.security.providers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class IesiProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {

    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();

    private List<AuthenticationProvider> providers = Collections.emptyList();

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private AuthenticationManager parent;

    private boolean eraseCredentialsAfterAuthentication = true;

    /**
     * Construct a {@link ProviderManager} using the given {@link AuthenticationProvider}s
     * @param providers the {@link AuthenticationProvider}s to use
     */
    public IesiProviderManager(AuthenticationProvider... providers) {
        this(Arrays.asList(providers), null);
    }

    /**
     * Construct a {@link ProviderManager} using the given {@link AuthenticationProvider}s
     * @param providers the {@link AuthenticationProvider}s to use
     */
    public IesiProviderManager(List<AuthenticationProvider> providers) {
        this(providers, null);
    }

    /**
     * Construct a {@link ProviderManager} using the provided parameters
     * @param providers the {@link AuthenticationProvider}s to use
     * @param parent a parent {@link AuthenticationManager} to fall back to
     */
    public IesiProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
        Assert.notNull(providers, "providers list cannot be null");
        this.providers = providers;
        this.parent = parent;
        checkState();
    }

    @Override
    public void afterPropertiesSet() {
        checkState();
    }

    private void checkState() {
        Assert.isTrue(this.parent != null || !this.providers.isEmpty(),
                "A parent AuthenticationManager or a list of AuthenticationProviders is required");
        Assert.isTrue(!CollectionUtils.contains(this.providers.iterator(), null),
                "providers list cannot contain null values");
    }

    /**
     * Attempts to authenticate the passed {@link Authentication} object.
     * <p>
     * The list of {@link AuthenticationProvider}s will be successively tried until an
     * <code>AuthenticationProvider</code> indicates it is capable of authenticating the
     * type of <code>Authentication</code> object passed. Authentication will then be
     * attempted with that <code>AuthenticationProvider</code>.
     * <p>
     * If more than one <code>AuthenticationProvider</code> supports the passed
     * <code>Authentication</code> object, the first one able to successfully authenticate
     * the <code>Authentication</code> object determines the <code>result</code>,
     * overriding any possible <code>AuthenticationException</code> thrown by earlier
     * supporting <code>AuthenticationProvider</code>s. On successful authentication, no
     * subsequent <code>AuthenticationProvider</code>s will be tried. If authentication
     * was not successful by any supporting <code>AuthenticationProvider</code> the last
     * thrown <code>AuthenticationException</code> will be rethrown.
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        AuthenticationException parentException = null;
        Authentication result = null;
        Authentication parentResult = null;
        int currentPosition = 0;
        int size = this.providers.size();
        for (AuthenticationProvider provider : getProviders()) {
            if (!provider.supports(toTest)) {
                continue;
            }
            if (log.isTraceEnabled()) {
                log.trace(LogMessage.format("Authenticating request with %s (%d/%d)",
                        provider.getClass().getSimpleName(), ++currentPosition, size));
            }
            try {
                result = provider.authenticate(authentication);
                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            }
            catch (AccountStatusException | InternalAuthenticationServiceException ex) {
                prepareException(ex, authentication);
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw ex;
            }
            catch (AuthenticationException ex) {
                lastException = ex;
                if (ex instanceof BadCredentialsException) {
                    throw ex;
                }
            }
        }
        if (result == null && this.parent != null) {
            // Allow the parent to try.
            try {
                parentResult = this.parent.authenticate(authentication);
                result = parentResult;
            }
            catch (ProviderNotFoundException ex) {
                // ignore as we will throw below if no other exception occurred prior to
                // calling parent and the parent
                // may throw ProviderNotFound even though a provider in the child already
                // handled the request
            }
            catch (AuthenticationException ex) {
                parentException = ex;
                lastException = ex;
            }
        }
        if (result != null) {
            if (this.eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)) {
                // Authentication is complete. Remove credentials and other secret data
                // from authentication
                ((CredentialsContainer) result).eraseCredentials();
            }
            // If the parent AuthenticationManager was attempted and successful then it
            // will publish an AuthenticationSuccessEvent
            // This check prevents a duplicate AuthenticationSuccessEvent if the parent
            // AuthenticationManager already published it
            if (parentResult == null) {
                this.eventPublisher.publishAuthenticationSuccess(result);
            }

            return result;
        }

        // Parent was null, or didn't authenticate (or throw an exception).
        if (lastException == null) {
            lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound",
                    new Object[] { toTest.getName() }, "No AuthenticationProvider found for {0}"));
        }
        // If the parent AuthenticationManager was attempted and failed then it will
        // publish an AbstractAuthenticationFailureEvent
        // This check prevents a duplicate AbstractAuthenticationFailureEvent if the
        // parent AuthenticationManager already published it
        if (parentException == null) {
            prepareException(lastException, authentication);
        }
        throw lastException;
    }

    @SuppressWarnings("deprecation")
    private void prepareException(AuthenticationException ex, Authentication auth) {
        this.eventPublisher.publishAuthenticationFailure(ex, auth);
    }

    /**
     * Copies the authentication details from a source Authentication object to a
     * destination one, provided the latter does not already have one set.
     * @param source source authentication
     * @param dest the destination authentication object
     */
    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;
            token.setDetails(source.getDetails());
        }
    }

    public List<AuthenticationProvider> getProviders() {
        return this.providers;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    /**
     * If set to, a resulting {@code Authentication} which implements the
     * {@code CredentialsContainer} interface will have its
     * {@link CredentialsContainer#eraseCredentials() eraseCredentials} method called
     * before it is returned from the {@code authenticate()} method.
     * @param eraseSecretData set to {@literal false} to retain the credentials data in
     * memory. Defaults to {@literal true}.
     */
    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return this.eraseCredentialsAfterAuthentication;
    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {

        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {
        }

    }
}
