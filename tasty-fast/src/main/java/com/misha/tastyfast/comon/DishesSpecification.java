package com.misha.tastyfast.comon;


import com.misha.tastyfast.model.Dishes;
import org.springframework.data.jpa.domain.Specification;

public class DishesSpecification {

    public static Specification<Dishes> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
