package com.example.userservice.util;

import com.example.userservice.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory SESSION_FACTORY;

    static {
        try {
            StandardServiceRegistry registry =
                    new StandardServiceRegistryBuilder().configure().build();
            Metadata metadata = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .getMetadataBuilder()
                    .build();
            SESSION_FACTORY = metadata.getSessionFactoryBuilder().build();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down SessionFactory");
                SESSION_FACTORY.close();
            }));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to initialize SessionFactory: " + e.getMessage());
        }
    }

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }
}
