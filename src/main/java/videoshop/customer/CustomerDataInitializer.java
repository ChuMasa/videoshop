/*
q * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package videoshop.customer;

import java.util.List;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initalizes {@link Customer}s.
 *
 * @author Oliver Gierke
 */
@Component
@Order(10)
class CustomerDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerDataInitializer.class);

	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	/**
	 * Creates a new {@link CustomerDataInitializer} with the given {@link UserAccountManager} and
	 * {@link CustomerRepository}.
	 *
	 * @param userAccountManager must not be {@literal null}.
	 * @param customerRepository must not be {@literal null}.
	 */
	CustomerDataInitializer(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		// (｡◕‿◕｡)
		// UserAccounts bestehen aus einem Identifier und eine Password, diese werden auch für ein Login gebraucht
		// Zusätzlich kann ein UserAccount noch Rollen bekommen, diese können in den Controllern und im View dazu genutzt
		// werden
		// um bestimmte Bereiche nicht zugänglich zu machen, das "ROLE_"-Prefix ist eine Konvention welche für Spring
		// Security nötig ist.

		// Skip creation if database was already populated
		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}

		LOG.info("Creating default users and customers.");

		var password = UnencryptedPassword.of("123");

		var bossAccount = userAccountManager.create("boss", password, Role.of("ROLE_BOSS"));
		userAccountManager.save(bossAccount);

		var customerRole = Role.of("ROLE_CUSTOMER");

		var ua1 = userAccountManager.create("hans", password, customerRole);
		var ua2 = userAccountManager.create("dextermorgan", password, customerRole);
		var ua3 = userAccountManager.create("earlhickey", password, customerRole);
		var ua4 = userAccountManager.create("mclovinfogell", password, customerRole);

		var c1 = new Customer(ua1, "wurst");
		var c2 = new Customer(ua2, "Miami-Dade County");
		var c3 = new Customer(ua3, "Camden County - Motel");
		var c4 = new Customer(ua4, "Los Angeles");

		customerRepository.saveAll(List.of(c1, c2, c3, c4));
	}
}
