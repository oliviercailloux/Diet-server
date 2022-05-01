package io.github.oliviercailloux.diet.entity;

import java.util.function.Consumer;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@RequestScoped
class MyTE implements TransactionExecutor {

	@Inject
	EntityManager em;

	@Override
	@Transactional
	public void execute(Consumer<EntityManager> task) {
		task.accept(em);
	}
}