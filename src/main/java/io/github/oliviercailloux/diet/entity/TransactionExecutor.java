package io.github.oliviercailloux.diet.entity;

import java.util.function.Consumer;
import javax.persistence.EntityManager;

public interface TransactionExecutor {
	void execute(Consumer<EntityManager> task);
}
