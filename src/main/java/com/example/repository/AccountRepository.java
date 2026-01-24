package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.entity.Account;
import com.example.entity.AccountStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ApplicationScoped
public class AccountRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    CriteriaBuilder criteriaBuilder;

    public Account findBySub(String sub) {
        return entityManager.find(Account.class, sub);
    }

    public List<Account> findSearch(String query, AccountStatus status, int pageIndex, int pageSize) {
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
        Root<Account> root = criteriaQuery.from(Account.class);

        criteriaQuery.where(buildPredicates(root, query, status));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public long countAll() {
        return entityManager.createQuery("SELECT count(a) FROM Account a", Long.class).getSingleResult();
    }

    public long countByStatus(AccountStatus status) {
        return entityManager.createQuery("SELECT count(a) FROM Account a WHERE a.status = :s", Long.class)
                .setParameter("s", status)
                .getSingleResult();
    }

    public long countSearch(String query, AccountStatus status) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Account> root = criteriaQuery.from(Account.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(buildPredicates(root, query, status));

        return entityManager.createQuery(criteriaQuery)
                .getSingleResult();
    }

    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Account").executeUpdate();
    }

    public void persist(Account account) {
        entityManager.persist(account);
    }

    private Predicate[] buildPredicates(Root<Account> root, String query, AccountStatus status) {
        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.toLowerCase() + "%";
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("email"), pattern),
                    criteriaBuilder.like(root.get("fullName"), pattern)));
        }

        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
