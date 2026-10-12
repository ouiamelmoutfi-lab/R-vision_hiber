package org.example.service;

import org.example.dao.GenericDAO;
import org.example.entities.Produit;
import org.example.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProduitService extends CrudService<Produit, Long> {

    private final SessionFactory factory;

    public ProduitService() {
        super(Produit.class);
        this.factory = HibernateUtil.getSessionFactory();
    }

    public List<Produit> findByCategorie(Long catId) {
        Session session = factory.openSession();

        try {
            return session.createNamedQuery(
                            "Produit.findByCategorie", Produit.class)
                    .setParameter("catId", catId)
                    .getResultList();

        } finally {
            session.close();
        }
    }

    public List<Produit> search(
            String term,
            BigDecimal minPrix,
            BigDecimal maxPrix) {

        Session session = factory.openSession();

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Produit> cq = cb.createQuery(Produit.class);
            Root<Produit> root = cq.from(Produit.class);

            List<Predicate> predicates = new ArrayList<>();

            if (term != null && !term.trim().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("libelle")),
                                "%" + term.trim().toLowerCase() + "%"
                        )
                );
            }

            if (minPrix != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.<BigDecimal>get("prix"), minPrix
                        )
                );
            }

            if (maxPrix != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.<BigDecimal>get("prix"), maxPrix
                        )
                );
            }

            cq.where(predicates.toArray(new Predicate[0]));

            return session.createQuery(cq).getResultList();

        } finally {
            session.close();
        }
    }

    public List<Produit> findPage(int pageNumber, int pageSize) {

        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException(
                    "Le numéro de page et la taille doivent être positifs."
            );
        }

        Session session = factory.openSession();

        try {
            return session.createQuery(
                            "SELECT p FROM Produit p ORDER BY p.id",
                            Produit.class
                    )
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

        } finally {
            session.close();
        }
    }
}
