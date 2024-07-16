package com.misha.tastyfast.comon;

import com.misha.tastyfast.model.Drink;
import org.springframework.data.jpa.domain.Specification;

public class DrinkSpecification {
    public static Specification<Drink> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
