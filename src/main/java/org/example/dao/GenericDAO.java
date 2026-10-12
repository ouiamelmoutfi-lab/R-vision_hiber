package org.example.dao;

import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

public class GenericDAO<T, ID extends Serializable> {

    private final Class<T> entityClass;
    private final SessionFactory factory;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.factory = HibernateUtil.getSessionFactory();
    }

    public T find(ID id) {
        Session session = factory.openSession();

        try {
            return session.get(entityClass, id);
        } finally {
            session.close();
        }
    }

    public List<T> findAll() {
        Session session = factory.openSession();

        try {
            String hql = "FROM " + entityClass.getSimpleName();

            return session.createQuery(hql, entityClass)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    public void create(T entity) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            session.persist(entity);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            session.close();
        }
    }
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Object updated = session.merge(entity);
            tx.commit();

            return (T) updated;

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            session.close();
        }
    }

    public void delete(T entity) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            session.remove(session.contains(entity)
                    ? entity
                    : session.merge(entity));

            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            session.close();
        }
    }
}