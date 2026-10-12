package org.example;

import org.example.entities.Categorie;
import org.example.entities.Produit;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class App {

    public static void main(String[] args) {

        try {
            // 1. Créer le service des catégories
            CrudService<Categorie, Long> categorieService =
                    new CrudService<>(Categorie.class);

            // 2. Créer une catégorie
            Categorie categorie = new Categorie("Informatique");
            categorieService.create(categorie);

            System.out.println("Catégorie créée avec succès.");

            // 3. Créer le service des produits
            ProduitService produitService = new ProduitService();

            // 4. Créer un produit
            Produit produit = new Produit(
                    "Ordinateur",
                    new BigDecimal("7500.00"),
                    categorie
            );

            produitService.create(produit);

            System.out.println("Produit créé avec succès.");

            // 5. Afficher tous les produits
            List<Produit> produits = produitService.getAll();

            System.out.println("Liste des produits :");

            for (Produit p : produits) {
                System.out.println(p);
            }

            // 6. Rechercher les produits par catégorie
            List<Produit> produitsCategorie =
                    produitService.findByCategorie(categorie.getId());

            System.out.println("Produits de la catégorie Informatique :");

            for (Produit p : produitsCategorie) {
                System.out.println(p);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
