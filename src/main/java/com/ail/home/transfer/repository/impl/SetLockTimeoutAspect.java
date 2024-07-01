package com.ail.home.transfer.repository.impl;

import static java.util.Objects.requireNonNullElse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Aspect
@Component
@Profile("!test")
public class SetLockTimeoutAspect {

	private static final Long DEFAULT_LOCK_TIMEOUT_MS = 5000L;

	private final EntityManager em;
	private final Long pessimisticLockTimeout;

	public SetLockTimeoutAspect(final EntityManager em, @Value("${pessimistic-lock.timeout}") final Long pessimisticLockTimeout) {
		this.pessimisticLockTimeout = pessimisticLockTimeout;
		this.em = em;
	}

	/**
	 * Configures lock timeout for the current thread.
	 * See <a href="https://postgresqlco.nf/doc/en/param/lock_timeout/">lock_timeout</a> description.
	 *
	 * @param lockTimeoutMillis lock timeout in milliseconds.
	 */
	public void setLockTimeout(final Long lockTimeoutMillis) {
		final long timeout = requireNonNullElse(lockTimeoutMillis, requireNonNullElse(pessimisticLockTimeout, DEFAULT_LOCK_TIMEOUT_MS));
		// Since javax.persistence.lock.timeout is not supported by Hibernate for PostgreSQL, then we have to use native query.
		em.createNativeQuery("set local lock_timeout = " + timeout)
			.executeUpdate();
	}

	public void setDefaultLockTimeout() {
		setLockTimeout(null);
	}

	@Pointcut("@annotation(org.springframework.data.jpa.repository.Lock)")
	public void lockedRepositoryMethod() {
		// a pointcut
	}

	@Before("lockedRepositoryMethod()")
	public void beforeQuery(final JoinPoint joinPoint) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		final Method method = signature.getMethod();
		final Annotation methodAnnotation = method.getAnnotation(Lock.class);
		final String annotationString = methodAnnotation.toString();
		if (annotationString.contains(LockModeType.PESSIMISTIC_WRITE.name())
			|| annotationString.contains(LockModeType.PESSIMISTIC_FORCE_INCREMENT.name())) {
			setDefaultLockTimeout();
		}
	}
}
